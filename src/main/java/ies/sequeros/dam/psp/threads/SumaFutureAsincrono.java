package ies.sequeros.dam.psp.threads;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class SumaFutureAsincrono  {
    public static boolean isPrimo(int n) {
        boolean primo = true;
        for (int i = 2; i < n && primo; i++) {
            if (n % i == 0) {
                primo = false;
            }
        }
        return primo;
    }
   public static Integer calc(int[] datos) throws Exception {
        int resultado = 0;
        for (int i = 0; i <datos.length; i++) {
            if (isPrimo(datos[i])) {
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

        CompletableFuture<Integer> [] tareas= new CompletableFuture [nThreads];
        Random random = new Random();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = random.nextInt(98+2);
         }
        System.out.print("Comenzando...");
        int longitud=0;

        //se crean los futuros en el ejector para ejecución de forma asíncrona
        for (int i = 0; i < nThreads; i++) {
            longitud= trozo+ (i+1==nThreads?resto:0);
            int[] subvector = new int[longitud];
            //copia de los valores
            System.arraycopy(vector, i*trozo, subvector,0, longitud);
            //asincrono
            tareas[i]=CompletableFuture.supplyAsync(() -> {
                try {
                    return SumaFutureAsincrono.calc(subvector);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            // Se ejecuta de forma asíncrona al terminar, sin bloquear el hilo actual
            tareas[i].thenAccept(r -> {
                System.out.println("Resultado recibido asíncronamente: " + r);
            });
        }
        executor.shutdown();
        //se espera para ver el efecto
        Thread.sleep(1000000);
    }
}
