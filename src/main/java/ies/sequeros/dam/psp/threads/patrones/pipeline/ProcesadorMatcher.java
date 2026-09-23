package ies.sequeros.dam.psp.threads.patrones.pipeline;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.concurrent.BlockingQueue;

public  class ProcesadorMatcher implements Runnable {
    private final BlockingQueue<Personaje> colaEntrada;
    private final BlockingQueue<Personaje> colaSalida;

    public ProcesadorMatcher(BlockingQueue<Personaje> colaEntrada, BlockingQueue<Personaje> colaSalida) {
        this.colaEntrada = colaEntrada;
        this.colaSalida = colaSalida;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Personaje personaje = colaEntrada.take();
                if (personaje.getIdImagen().equals("FIN")) {
                    colaSalida.put(personaje);
                    break;
                }

                System.out.println("Procesando matrices y buscando coincidencias en " + personaje.getIdImagen());

                // se busca
                 //Mat src = opencv_imgcodecs.imread(personaje.getIdImagen(), 0); // Grayscale
                // opencv_imgproc.matchTemplate(src, template, result, opencv_imgproc.CV_TM_CCOEFF_NORMED);

                Thread.sleep(800); // Simulando procesamiento de CPU intenso

                // Simulación del resultado del matching
                if (personaje.getIdImagen().contains("homer")) {
                    personaje.setResultado("Homer Simpson", 0.92);
                } else {
                    personaje.setResultado("Bart Simpson", 0.85);
                }

                colaSalida.put(personaje);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}