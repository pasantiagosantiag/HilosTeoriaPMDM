package ies.sequeros.dam.psp.threads;

import java.util.Random;

public class SumaRunnable implements Runnable {
    private int[] datos;
    private int resultado;
    public SumaRunnable(int[] datos){
        this.datos = datos;
        this.resultado=0;
       // System.out.println("Iniciando SumaRunnable:"+this.hashCode());
    }
    public boolean isPrimo(int n){
        boolean primo = true;
        for (int i = 2; i < n && primo; i++) {
            if (n % i == 0) {
                primo = false;
            }
        }
        return primo;
    }

    @Override
    public void run() {
        for (int i = 0; i < datos.length; i++) {
            if(isPrimo(datos[i])){
                resultado++;
            }
        }

    }
    public int getResultado(){
        return resultado;
    }
    public static void main(String[] args) throws InterruptedException {
        int tam=Config.VECTOR_SIZE;
        int[] vector= new int[tam];

        int nThreads=Config.NTHREADS;
        int trozo=tam / nThreads;
        int resto= tam % nThreads;
        SumaRunnable[] tareas= new SumaRunnable[nThreads];
        Thread[] threads= new Thread[nThreads];
        long startTime,endTime ;
        long resultado=0;
        Random random = new Random();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = random.nextInt(98)+2;
        }

        System.out.print("Comenzando...");
        startTime = System.nanoTime();
        //se crean los hilos
        for (int i = 0; i < nThreads; i++) {
            //se obtiene el subvector del tamaño
            int longitud= trozo+ (i+1==nThreads?resto:0);

            int[] subvector = new int[longitud];
            //copia de los valores
            System.arraycopy(vector, i*trozo, subvector,0, longitud);
            SumaRunnable tarea= new SumaRunnable(subvector);
            //lanzar los hilos y guardarlos para obtener posteriormente la información
            Thread thread = new Thread(tarea);
            tareas[i]=tarea;
            threads[i] = thread;
        }
        //se inicia
        for (int i = 0; i < nThreads; i++) {
           // System.out.println("iniciando "+i);
            threads[i].start();
        }
        //se espera a que todos terminen
        for (int i = 0; i < nThreads; i++) {

            threads[i].join();
            resultado+=tareas[i].getResultado();
        }
        endTime = System.nanoTime();
        double segundos =  (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Tiempo: " + segundos + " segundos");
       }
}
