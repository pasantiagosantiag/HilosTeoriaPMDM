package ies.sequeros.dam.psp.threads.patrones.pipeline;



import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class LectorImagenes  implements Runnable {
    private final BlockingQueue<ImagenAnalizar> colaSalida;
    private final List<String> rutas;
    public LectorImagenes(BlockingQueue<ImagenAnalizar> colaSalida, List<String> rutas) {
        this.colaSalida = colaSalida;
        this.rutas = rutas;
    }

    @Override
    public void run() {


        for (String imgPath : this.rutas) {
            System.out.println("[Lector] Cargando imagen: " + imgPath);
            java.io.File archivo = new java.io.File(imgPath);
            if (!archivo.exists() || !archivo.isFile()) {
                System.err.println("[Error] El archivo no existe o la ruta es inválida: " + imgPath);
                continue;
            }
              Mat img = Imgcodecs.imread(archivo.getAbsolutePath().toString(),Imgcodecs.IMREAD_GRAYSCALE);

            // 3. Comprobar puntero nulo seguro (SIN usar img.empty() si el puntero es NULL)
            if (img == null  || img.empty() ) {
                System.err.println("[Error] OpenCV no pudo decodificar la imagen: " + imgPath);
                continue;
            }



            ImagenAnalizar imagenAnalizar = new ImagenAnalizar(imgPath);
            imagenAnalizar.setImagen(img); // Asignación directa sin .clone()

            System.out.println("Pasando a analizar: " + imgPath);
            try {
                this.colaSalida.put(imagenAnalizar);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }}

