package ies.sequeros.dam.psp.threads;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;


public class ThreadStates {

    public ThreadStates() {
    }
    public static void printMenu() {
        System.out.println("Menu de opciones\n");
        System.out.println("0. Salir");
        System.out.println("1. Iniciar hilo");
        System.out.println("2. Pasa a espera");
        System.out.println("3. Salir de espera");
        System.out.println("4. Salir de hilo");
        System.out.println("5. Consultar estado");
    }

    public static void main(final String[] args) throws InterruptedException {
        final ChildThread ct = new ChildThread();



        int opcion = 0;
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

        do {
            ThreadStates.printMenu();
            opcion = sc.nextInt();
            switch (opcion) {
                case 0:
                    break;
                case 1:
                    ct.start();
                    System.out.println("El estado es" + ct.getState().name());
                    break;
                case 2:
                    ct.wwait();
                    System.out.println("El estado es: " + ct.getState().name());
                    break;
                case 3:
                    ct.nnotify();
                    System.out.println("El estado es: " + ct.getState().name());
                    break;
                case 4:
                    ct.bye();
                    System.out.println("El estado es" + ct.getState().name());
                    break;
                case 5:
                    System.out.println("El estado es" + ct.getState().name());
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        } while (0 != opcion);

    }

    public static class ChildThread extends Thread {
        //atomico para lecturas y escrituras simples, se escribe/lee directamente de memoria
        private volatile int state=1;
        private boolean wait=false;
        public ChildThread() {
        }
        public void wwait() {
            //para evitar que wait sea escrito/leido de forma
            //inconsistente
            synchronized (this) {
            this.wait = true;
            }
        }
        public void nnotify() {
            //para evitar que wait sea escrito/leido de forma
            //inconsistente
            synchronized (this) {
                this.wait = false;
                this.notify();
            }
        }
        @Override
        public void run() {
            while (1 == this.state) {
                try {
                    //para evitar que wait sea escrito/leido de forma
                    //inconsistente
                    synchronized (this) {
                        if (wait) {
                            wait();
                        }
                    }
                    //duerme
                    Thread.sleep(400);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

            }
        }

        public void bye() {
            //al ser atomico no es necesario bloquear
            state = 0;
        }
    }
}
