package ies.sequeros.dam.psp.threads.patrones.pipeline;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Principal {
    public static void main(String[] args) throws IOException, InterruptedException {
        String ruta_test="./images_test";
        String ruta_repositorio="./images_repository";
        OpenCV.loadLocally();

        //se cargan las imágenes de test en una lista
        Path path=Paths.get(ruta_test);
        if (Files.notExists(path) ||  !Files.isDirectory(path) ){
            throw  new IOException("El archivo no existe o no es un directorio");
        }
        List<String> listaTest=Files.walk(path).filter(f-> {
            try {return Files.isRegularFile(f) && RepositorioPlantilla.isImage(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //se hace en paralelo, con hilos
        }).parallel().map( f1-> {return f1.toAbsolutePath().toString();
        }).collect(Collectors.toUnmodifiableList());

        //se carga el repositorio si todo es correcto
        RepositorioPlantilla repositorioPlantilla= new RepositorioPlantilla(ruta_repositorio);

        //se crean las estructura para pasar los datos, observar que son bloqueantes
        BlockingQueue<ImagenAnalizar> principalToLector = new LinkedBlockingQueue<>();
        //se le da capacidad 5 para que se observe como se bloquean
        BlockingQueue<ImagenAnalizar> lectorToProcesador = new LinkedBlockingQueue<>(5);
        BlockingQueue<ImagenAnalizar> procesadorToSalida = new LinkedBlockingQueue<>(5);
        //se crean los hilos y se pasan los parametros
        Thread t1 = new Thread(new LectorImagenes(lectorToProcesador,listaTest), "Hilo-Lector");
        Thread t2 = new Thread(new ProcesadorMatcher(repositorioPlantilla,lectorToProcesador,procesadorToSalida), "Hilo-Procesador");
        Thread t3 = new Thread(new PresentadorResultados(procesadorToSalida), "Hilo-Resultados");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}
