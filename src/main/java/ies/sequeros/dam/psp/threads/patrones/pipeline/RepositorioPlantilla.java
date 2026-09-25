package ies.sequeros.dam.psp.threads.patrones.pipeline;





import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RepositorioPlantilla {
    private List<PlantillaPersonaje> plantillas;
    private String path;

    public RepositorioPlantilla(String ruta) throws IOException {
        //comprobaciones
        if (Files.notExists(Paths.get(ruta))) {
            throw new IOException("El archivo no existe");
        }
        Path path = Paths.get(ruta);
        if (!Files.isDirectory(path)) {
            throw new IOException("El archivo es un directorio");
        }

        plantillas = new LinkedList<>();
        //obtener las plantillas

        try {
            //se obtiene las plantillas
            //primero los path de las imagenes del directorio
            //y en paralelo, se cargan las imagenes
            //por último se transforman en una colección y listo
            this.plantillas = Files.walk(path).filter(f -> {
                try {
                    return Files.isRegularFile(f) && RepositorioPlantilla.isImage(f);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).parallel().map(path1 -> {
                String nombre = path1.getFileName().toString();
                Mat img = Imgcodecs.imread(path1.toString(), Imgcodecs.IMREAD_GRAYSCALE);
                String nombreSinExtension = nombre.contains(".")
                        ? nombre.substring(0, nombre.lastIndexOf('.'))
                        : nombre;
                PlantillaPersonaje plantillaPersonaje = new PlantillaPersonaje(img, nombreSinExtension);
                return plantillaPersonaje;
            }).collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<PlantillaPersonaje> getPlantillas() {
        return this.plantillas;
    }
    public static boolean isImage(Path p) throws IOException {
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
    private void load(String ruta) throws IOException {
        Path path = Paths.get(ruta);
        try (Stream<Path> stream = Files.walk(path)) {
            // Obtener las rutas, no hace falta paralelo
            List<Path> archivosImagen = stream
                    .filter(Files::isRegularFile)
                    .filter(f -> {
                        try {
                            return RepositorioPlantilla.isImage(f);
                        } catch (IOException e) {
                            return false; // Evitamos romper el flujo con RuntimeException
                        }
                    })
                    .toList();

            // Cargr las imágenes para OpenCV en PARALELO (CPU-bound)
            this.plantillas = archivosImagen.parallelStream()
                    .map(path1 -> {
                        Mat img = Imgcodecs.imread(path1.toString(), Imgcodecs.IMREAD_GRAYSCALE);

                        // Validar que OpenCV haya decodificado la imagen correctamente
                        if (img == null || img.empty()) {
                            System.err.println("[Aviso] No se pudo decodificar la plantilla: " + path1);
                            return null;
                        }

                        String nombre = path1.getFileName().toString();
                        String nombreSinExtension = nombre.contains(".")
                                ? nombre.substring(0, nombre.lastIndexOf('.'))
                                : nombre;

                        return new PlantillaPersonaje(img, nombreSinExtension);
                    })
                    .filter( Objects::nonNull) // Eliminar las plantillas que hayan fallado al leer
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            System.err.println("Error al acceder al directorio de plantillas: " + e.getMessage());
            this.plantillas = List.of();
        }

    }
}
