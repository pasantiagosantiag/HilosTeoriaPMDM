package ies.sequeros.dam.psp.threads.patrones.pipeline;


import org.opencv.core.Mat;

public class PlantillaPersonaje {
    private Mat imagen;
    private String nombre;
    public PlantillaPersonaje(Mat imagen, String nombre) {
        this.imagen = imagen;
        this.nombre = nombre;
    }
    public Mat getImagen() {
        return imagen;
    }
    public void setImagen(Mat imagen) {
        this.imagen = imagen;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
