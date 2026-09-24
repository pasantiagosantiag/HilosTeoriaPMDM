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
        String ruta_test="";
        String ruta_repositorio="./images_repository";
        OpenCV.loadLocally();
        /*Mat imagen = Imgcodecs.imread("/home/pedro/IdeaProjects/HilosTeoriaPMDM/images_test/a.jpg");

        if (imagen.empty()) {
            System.err.println("Error: No se pudo cargar la imagen. Revisa la ruta.");
            return;
        }

        // 5. Mostrar información básica de la imagen en consola
        System.out.println("¡Imagen cargada con éxito!");
        System.out.println("Ancho (columnas): " + imagen.cols() + " px");
        System.out.println("Alto (filas): " + imagen.rows() + " px");
        System.out.println("Canales (ej. 3 para BGR): " + imagen.channels());*/

        System.setProperty("org.bytedeco.javacpp.norgc", "true");


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
        }).parallel().map( f1-> {return f1.toAbsolutePath().toString();
        }).collect(Collectors.toUnmodifiableList());

        //se carga el repositorio si todo es correcto
        RepositorioPlantilla repositorioPlantilla= new RepositorioPlantilla(ruta_repositorio);
        repositorioPlantilla.getPlantillas().forEach( plantilla -> {
            System.out.println(plantilla.getNombre());
        });
        //se crean las estructura para pasar los datos, observar que son bloqueantes
        BlockingQueue<ImagenAnalizar> principalToLector = new LinkedBlockingQueue<>();
        BlockingQueue<ImagenAnalizar> lectorToProcesador = new LinkedBlockingQueue<>();
        BlockingQueue<ImagenAnalizar> procesadorToSalida = new LinkedBlockingQueue<>();
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
