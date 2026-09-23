package ies.sequeros.dam.psp.threads.patrones.master_worker;

import java.util.Random;
import java.util.concurrent.*;

public class FactorialExecutor {
    public static void main(String[] args) throws InterruptedException {
    BlockingQueue<Tarea> colaTareas = new LinkedBlockingQueue<>();
    Random random = new Random();
    int tareasIniciales=1000;
    int numWorkers = 5;//Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(numWorkers);
    //se crean las tareas
    for (int i = 0; i < tareasIniciales; i++) {
        colaTareas.add(new Tarea(i,Math.abs(random.nextInt(3000))));
    }
        //se crean tantas tareas como hilos
        for (int i= 0; i < numWorkers*2; i++) {
            //se crean las tareas, lamdas
            executor.submit(() -> {
                Tarea tarea = null;
                Boolean terminar = false;
                //mientras queden elementos para procesar se van leyendo de la cola
                while (!terminar) {
                    try {
                        tarea = colaTareas.take();//take bloquea //poll() no bloquea;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    //para que salga
                    long resultado = tarea.getNumero();
                    if (resultado == -1) {
                        terminar = true;
                    } else {
                        for (int j = tarea.getNumero() - 1; j >= 1; j--) {
                            resultado += j;
                        }
                        String nombreHilo = Thread.currentThread().getName();
                        System.out.println("Soy el hilo:" + nombreHilo + " ejecutando la tarea: " + tarea.getId() + " se ha procesado: " + tarea.getNumero() + " con resultado:" + resultado);
                    }

                }
                System.out.println("Terminando el hilo....");

            });
        }
    //no se admiten mas hilos
    executor.shutdown();

    //se generan tareas durante la ejecución, que se asignaran
    //a diferentes hilos
        for(int i=0;i<100;i++){
        Tarea tarea =null;
        tarea = new Tarea(tareasIniciales+i,Math.abs(random.nextInt(1000)));
        colaTareas.add(tarea);
        Thread.sleep(100);
    }
    //para que finalicen
        for(int i=0;i<numWorkers;i++){
        Tarea tarea =null;
        tarea = new Tarea(tareasIniciales+i,-1);
        colaTareas.add(tarea);

    }
        boolean finalizadoATiempo = executor.awaitTermination(4, TimeUnit.MINUTES);

        if (finalizadoATiempo) {
            System.out.println("[Master] Todas las tareas han finalizado correctamente.");
        }
    }
}
