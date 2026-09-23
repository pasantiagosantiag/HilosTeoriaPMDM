package ies.sequeros.dam.psp.threads.patrones.productor_consumidor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class ProductorConsumidorColaBloqueante {
    private static final int CAPACIDAD = 5;
    private static final int NUMEROITEMS=100;
    private static final ArrayBlockingQueue<Integer> cola = new ArrayBlockingQueue<>(CAPACIDAD);

    public static void main(String[] args){
        Runnable tareaProductor = () -> {
            try {
                for (int i = 1; i <= NUMEROITEMS; i++) {
                    //se bloquea en caso de estar lleno
                    cola.put(i);
                    System.out.println("Producido: " + i + " | Tamaño cola: " + cola.size());
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                System.out.println("Productor interrumpido");
            }
        };
        //  Consumidor
        Runnable tareaConsumidor = () -> {
            try {
                for (int i = 1; i <= NUMEROITEMS; i++) {
                    int valor = cola.take();
                    System.out.println("Consumido: " + valor + " | Tamaño cola: " + cola.size());
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                System.out.println("Consumidor interrumpido");
            }
        };

        // Crear los hilos
        Thread hiloProductor = new Thread(tareaProductor);
        Thread hiloConsumidor = new Thread(tareaConsumidor);

        // Ejecutar
        hiloProductor.start();
        hiloConsumidor.start();
    }
}
