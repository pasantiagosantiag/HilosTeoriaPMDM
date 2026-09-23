package ies.sequeros.dam.psp.threads.patrones.pipeline;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.concurrent.BlockingQueue;

public class LectorImagenes  implements Runnable {
    private final BlockingQueue<Personaje> colaSalida;

    public LectorImagenes(BlockingQueue<Personaje> colaSalida) {
        this.colaSalida = colaSalida;
    }

    @Override
    public void run() {
        try {
            String[] imagenes = {"frame_homer_01.jpg", "frame_bart_02.jpg", "frame_homer_03.jpg"};

            for (String imgPath : imagenes) {
                System.out.println("📂 [Lector CV] Cargando imagen: " + imgPath);
                 Mat img = opencv_imgcodecs.imread(imgPath);

                Personaje personaje = new Personaje(imgPath);
                personaje.setImagen(img);
                colaSalida.put(personaje);

            }
            colaSalida.put(new Personaje("FIN")); // Poison Pill
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
