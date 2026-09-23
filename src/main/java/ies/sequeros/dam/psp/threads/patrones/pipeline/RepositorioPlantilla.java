package ies.sequeros.dam.psp.threads.patrones.pipeline;



import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class RepositorioPlantilla {
    private List<PlantillaPersonaje> plantillas;
    private String path;
    public RepositorioPlantilla(String ruta) throws IOException {
        //comprobaciones
        if (Files.notExists(Paths.get(ruta))) {
            throw  new IOException("El archivo no existe");
        }
        Path path = Paths.get(ruta);
        if (!Files.isDirectory(path)){
            throw new IOException("El archivo es un directorio");
        }

        plantillas = new LinkedList<>();
        //obtener las plantillas

        try {
            //se obtiene las plantillas
            //primero los path de las imagenes del directorio
            //y en paralelo, se cargan las imagenes
            //por último se transforman en una colección y listo
            this.plantillas=Files.walk(path).filter( f-> {
                try {return Files.isRegularFile(f) && RepositorioPlantilla.isImage(f);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).parallel().map(path1->{
                String nombre=path1.getFileName().toString();
                Mat img = opencv_imgcodecs.imread(path1.toString());
                String nombreSinExtension = nombre.contains(".")
                        ? nombre.substring(0, nombre.lastIndexOf('.'))
                        : nombre;
                PlantillaPersonaje plantillaPersonaje=new PlantillaPersonaje(img,nombreSinExtension);
                return plantillaPersonaje;
            }).collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<PlantillaPersonaje> getPlantillas() {
        return this.plantillas;
    }


    private static boolean isImage(Path p) throws IOException {
        if (Files.notExists(p)){
            throw  new RuntimeException("El archivo no existe");
        }
        if (Files.isDirectory(p)){
            return false;
        }
        String tipoMime = Files.probeContentType(p);
        if (tipoMime != null && tipoMime.startsWith("image/")) {
           return true;
        } else {
            return false;

        }

    }
}
