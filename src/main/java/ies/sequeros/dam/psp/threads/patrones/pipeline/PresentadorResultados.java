package ies.sequeros.dam.psp.threads.patrones.pipeline;

import java.util.concurrent.BlockingQueue;

public class PresentadorResultados implements Runnable {
    private final BlockingQueue<ImagenAnalizar> colaEntrada;

    public PresentadorResultados(BlockingQueue<ImagenAnalizar> colaEntrada) {
        this.colaEntrada = colaEntrada;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ImagenAnalizar tarea = colaEntrada.take();
                if (tarea.getIdImagen().equals("FIN")) break;

                System.out.println("[Resultado Final] Imagen: " + tarea.getIdImagen()
                        + " -> Personaje: " + tarea.getPersonajeDetectado()
                        + " (Confianza: " + (tarea.getNivelCoincidencia() * 100) + "%)");
            }
            System.out.println("Análisis completado.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}