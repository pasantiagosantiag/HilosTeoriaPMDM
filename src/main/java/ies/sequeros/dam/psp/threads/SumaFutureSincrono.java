package ies.sequeros.dam.psp.threads;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class SumaFutureSincrono implements Callable<Integer> {
    private int[] datos;
    public SumaFutureSincrono(int[] datos) {
       this.datos=datos;
    }
    public boolean isPrimo(int n) {
        boolean primo = true;
        for (int i = 2; i < n && primo; i++) {
            if (n % i == 0) {
                primo = false;
            }
        }
        return primo;
    }
    @Override
    public Integer call() throws Exception {
        int resultado = 0;
        for (int i = 0; i <this.datos.length; i++) {
            if (isPrimo(this.datos[i])) {
                resultado++;

            }
        }
        return resultado;
    }
    public static void main(String[] args) throws InterruptedException {
        int tam = Config.VECTOR_SIZE;
        int[] vector = new int[tam];

        int nThreads = Config.NTHREADS;
        int trozo = tam / nThreads;
        int resto = tam % nThreads;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads/3);
        SumaFutureSincrono[] tareas= new SumaFutureSincrono[nThreads];
        long startTime, endTime;
        long resultado = 0;
        Random random = new Random();
        //se crean las clases
        for (int i = 0; i < vector.length; i++) {
            vector[i] = random.nextInt(98+2);
         }
        System.out.print("Comenzando...");
        startTime = System.nanoTime();
        int longitud=0;

        //se crean las clases
        for (int i = 0; i < nThreads; i++) {
            longitud= trozo+ (i+1==nThreads?resto:0);
            int[] subvector = new int[longitud];
            //copia de los valores
            System.arraycopy(vector, i*trozo, subvector,0, longitud);
            tareas[i]=new SumaFutureSincrono(subvector);
        }
        //se le pasa al ejecutor
        List<Future<Integer>> futuros = executor.invokeAll(List.of(tareas));
        // Han finalizado todos, se pueden obtener los valores
        for (Future<Integer> futuro : futuros) {
            try {
                resultado+=futuro.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        executor.shutdown();
        endTime = System.nanoTime();
        double segundos =  (endTime - startTime) / 1_000_000_000.0;

        System.out.println("Tiempo: " + segundos + " segundos");
        System.out.println("Resultado:"+resultado+"Ha tardado "+segundos+" segundos");
    }
}
