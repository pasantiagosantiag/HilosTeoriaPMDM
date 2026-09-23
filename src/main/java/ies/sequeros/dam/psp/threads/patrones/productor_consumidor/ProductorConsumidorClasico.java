package ies.sequeros.dam.psp.threads.patrones.productor_consumidor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ProductorConsumidorClasico {
    private static final Queue<Integer> cola = new LinkedList<>();
    private static final int CAPACIDAD = 5;
    private static final int NUMEROITEMS=100;
    // Semáforos para el control de flujo y exclusión mutua
    private static final Semaphore semProductor = new Semaphore(CAPACIDAD);
    private static final Semaphore semConsumidor = new Semaphore(0);
    //acceso a la estructura
    private static final Semaphore mutex = new Semaphore(1);
    public static void main(String[] args){
        Runnable tareaProductor = () -> {
            try {
                for (int i = 1; i <= NUMEROITEMS; i++) {
                    //comprueba que no esta lleno semProductro>0, sino bloquea
                    semProductor.acquire();
                    //bloquea la lista
                    mutex.acquire();

                    cola.add(i);
                    System.out.println("Producido: " + i + " | Tamaño cola: " + cola.size());
                    //se desbloquea la lista
                    mutex.release();
                    //anyade uno al consumidor, en caso de tener alguno bloqueado, se desbloquea
                    semConsumidor.release();

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
                    //se comprueba que existe algun elemento en el buffer
                    semConsumidor.acquire();
                    //se bloquea el buffer
                    mutex.acquire();

                    int valor = cola.poll();
                    System.out.println("Consumido: " + valor + " | Tamaño cola: " + cola.size());
                    //se libera el buffer
                    mutex.release();
                    //se informa de que existe un nuevo hueco en el buffer, en caso
                    //de que el producto este bloqueado, se desbloquea
                    semProductor.release();

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
