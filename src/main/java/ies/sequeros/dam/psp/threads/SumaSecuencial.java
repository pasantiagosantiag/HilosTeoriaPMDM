package ies.sequeros.dam.psp.threads;

import java.util.Random;

public class SumaSecuencial {
    public static boolean isPrimo(int n){
        boolean primo = true;
        for (int i = 2; i < n && primo; i++) {
            if (n % i == 0) {
                primo = false;
            }
        }
        return primo;
    }
    public static void main(String[] args) throws InterruptedException {
        int tam=Config.VECTOR_SIZE;
        int[] vector= new int[tam];
        long startTime,endTime ;
        long resultado=0;
        Random random = new Random();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = random.nextInt(98)+2;
        }
        System.out.print("Comenzando...");
        startTime = System.nanoTime();
        for (int i = 0; i < vector.length; i++) {
            if(isPrimo(vector[i])){
            resultado ++;
            }
        }
        endTime = System.nanoTime();
        double segundos =  (endTime - startTime) / 1_000_000_000.0;

        System.out.println("Tiempo: " + segundos + " segundos");
       //ystem.out.println("Resultado:"+resultado+"Ha tardado "+segundos+" segundos");
    }
}
