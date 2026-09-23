package ies.sequeros.dam.psp.threads.patrones.master_worker;

public  class Tarea {
    private final int id;
    private final int numero;
    public Tarea(int id, int numero) {
        this.id = id;
        this.numero = numero;
    }
    public int getNumero() { return numero; }
    public int getId() { return id; }
}