package ies.sequeros.dam.psp.threads.patrones.pipeline;

import java.util.concurrent.BlockingQueue;

public class PresentadorResultados implements Runnable {
    private final BlockingQueue<Personaje> colaEntrada;

    public PresentadorResultados(BlockingQueue<Personaje> colaEntrada) {
        this.colaEntrada = colaEntrada;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Personaje tarea = colaEntrada.take();
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