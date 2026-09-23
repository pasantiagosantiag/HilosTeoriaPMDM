package ies.sequeros.dam.psp.threads.patrones.lector_escritor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LectorEscritor {
    public static class CeldaProtegida{
        private int valor;
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        public CeldaProtegida(int valor){
            this.valor = valor;
        }
        public int getValor() {
            // Múltiples hilos pueden llamar a getValor() a la vez
            lock.readLock().lock();
            try {
                return valor;
            } finally {
                lock.readLock().unlock();
            }
        }

        public void setValor(int nuevoValor) {
            // Solo un hilo puede escribir, bloqueando lecturas y otras escrituras
            lock.writeLock().lock();
            try {
                this.valor = nuevoValor;
            } finally {
                lock.writeLock().unlock();
            }
        }

    }

    public static void main(String[] args) {
        List<CeldaProtegida> recurso= new ArrayList<CeldaProtegida>();
        int tamArray=10;
        for(int i = 0; i < tamArray; i++){
            recurso.add(new CeldaProtegida(i));
        }
        Runnable escritor= new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int index;
                int tiempoEspera;
                int valor;
                for(int i = 0; i < 100; i++){
                    index=random.nextInt(0,tamArray);
                    valor=random.nextInt();
                    recurso.get(index).setValor(valor);
                    System.out.println("Soy el "+Thread.currentThread().getName()+" ESCRITOR y se ha modificado "+index);
                    tiempoEspera=random.nextInt(1000,10000);
                    try {
                        Thread.sleep(tiempoEspera);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        Runnable lector= ()->{
            Random random = new Random();
            int index;
            int tiempoEspera;
            for(int i = 0; i < 100; i++){
                index=random.nextInt(0,tamArray);
                int valor=recurso.get(index).getValor();
                System.out.println("Soy el "+Thread.currentThread().getName()+"LECTOR y se ha leido "+index);
                //el tiempo de espera es menor, se producen mas lecturas
                tiempoEspera=random.nextInt(1000,1500);
                try {
                    Thread.sleep(tiempoEspera);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        //se crean los hilos
        Thread hiloEscritor1= new Thread(escritor);
        Thread hiloEscritor2= new Thread(escritor);
        Thread hiloLector1= new Thread(lector);
        Thread hiloLector2= new Thread(lector);
        //se lanzan
        hiloEscritor1.start();
        hiloEscritor2.start();
        hiloLector1.start();
        hiloLector2.start();
        //se espera a que finalice
        try {
            // Obligas al hilo principal a esperar a cada uno de ellos
            hiloEscritor1.join();
            hiloEscritor2.join();
            hiloLector1.join();
            hiloLector2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("El hilo principal fue interrumpido mientras esperaba.");
        }
        System.out.println("Todos los escritores y lectores han terminado. Fin del programa.");
    }
}
