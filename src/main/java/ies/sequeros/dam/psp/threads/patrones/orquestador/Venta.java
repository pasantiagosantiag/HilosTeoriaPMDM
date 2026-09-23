package ies.sequeros.dam.psp.threads.patrones.orquestador;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Venta {

    public record Pedido(int unidades, float precio, String nombreProducto, String mailUsuario){}

    public static void main(String[] args) {
        BlockingQueue<Pedido> colaPedidos = new LinkedBlockingQueue<>();
        AtomicBoolean run = new AtomicBoolean(true);
        Thread facturacion;
        Thread almacen;
        Thread ventas;
        Thread servidorWeb;
        servidorWeb= new Thread(() -> {
            while (run.get()) {
                Pedido pedido = colaPedidos.poll();
            }

        });



    }
}
