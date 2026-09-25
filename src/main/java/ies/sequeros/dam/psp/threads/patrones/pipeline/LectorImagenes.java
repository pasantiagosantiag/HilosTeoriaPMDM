package ies.sequeros.dam.psp.threads.patrones.pipeline;



import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LectorImagenes  implements Runnable {
    private final BlockingQueue<ImagenAnalizar> colaSalida;
    private final List<String> rutas;
    public LectorImagenes(BlockingQueue<ImagenAnalizar> colaSalida, List<String> rutas) {
        this.colaSalida = colaSalida;
        this.rutas = rutas;
    }

    @Override
    public void run() {
        //  Asignar un número razonable de hilos para I/O (p. ej., el doble de núcleos o un valor fijo como 8)
        int numHilos = Math.max(2, Runtime.getRuntime().availableProcessors() * 2-2);

        try (ExecutorService executor = Executors.newFixedThreadPool(numHilos)) {

            for (String imgPath : this.rutas) {
                // Enviar la carga de cada imagen como una tarea independiente al pool
                executor.submit(() -> procesarCargaImagen(imgPath));
            }

            // Ordenar al executor que no acepte más tareas y esperar a que terminen las pendientes
            executor.shutdown();
            boolean terminaron = executor.awaitTermination(1, TimeUnit.HOURS);
            if (!terminaron) {
                System.err.println("[Lector] Aviso: El tiempo de espera expiró antes de cargar todas las imágenes.");
            }

        } catch (InterruptedException e) {
            System.err.println("[Lector] Proceso de lectura interrumpido.");
            Thread.currentThread().interrupt();
        }

        System.out.println("[Lector] Finalizada la carga e inserción de todas las imágenes.");
    }

    private void procesarCargaImagen(String imgPath) {
        File archivo = new File(imgPath);
        if (!archivo.exists() || !archivo.isFile()) {
            //salta el error, pero continua el programa, no se lanza la excepción
            System.err.println("[Error] El archivo no existe o la ruta es inválida: " + imgPath);
            return;
        }

        System.out.println("[Lector] Cargando imagen en hilo " + Thread.currentThread().getName() + ": " + imgPath);

        // Imgcodecs.imread es thread-safe siempre que cada hilo genere su propio objeto Mat
        Mat img = Imgcodecs.imread(archivo.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

        if (img == null || img.empty()) {
            System.err.println("[Error] OpenCV no pudo decodificar la imagen: " + imgPath);
            return;
        }
        //objeto que se inserta el na cola
        ImagenAnalizar imagenAnalizar = new ImagenAnalizar(imgPath);
        imagenAnalizar.setImagen(img);

        try {
            // BlockingQueue.put() es thread-safe y bloqueará si la cola está llena de forma segura
            this.colaSalida.put(imagenAnalizar);
            System.out.println("Pasando a analizar: " + imgPath);
        } catch (InterruptedException e) {
            System.err.println("[Lector] Hilo interrumpido mientras insertaba en la cola: " + imgPath);
            img.release(); // Liberar memoria nativa si no pudo ser insertada
            Thread.currentThread().interrupt();
        }
    }
    }

