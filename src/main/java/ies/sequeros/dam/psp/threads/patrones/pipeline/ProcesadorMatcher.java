package ies.sequeros.dam.psp.threads.patrones.pipeline;



import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class ProcesadorMatcher implements Runnable {
    private class ResultadoComparar {
        private PlantillaPersonaje plantilla;
        private ImagenAnalizar imagenOriginal;
        private double valor;

        public ResultadoComparar(PlantillaPersonaje plantilla, ImagenAnalizar imagenOriginal, double valor) {
            this.plantilla = plantilla;
            this.imagenOriginal = imagenOriginal;
            this.valor = valor;
        }

        public PlantillaPersonaje getPlantilla() {
            return plantilla;
        }

        public ImagenAnalizar getImagenOriginal() {
            return imagenOriginal;
        }

        public double getValor() {
            return valor;
        }

        public void setValor(double valor) {
            this.valor = valor;
        }

        public void setPlantilla(PlantillaPersonaje plantilla) {
            this.plantilla = plantilla;
        }

        public void setImagenOriginal(ImagenAnalizar imagenOriginal) {
            this.imagenOriginal = imagenOriginal;
        }
    }

    private final BlockingQueue<ImagenAnalizar> colaEntrada;
    private RepositorioPlantilla repositorioPlantilla;
    private final BlockingQueue<ImagenAnalizar> colaSalida;

    public ProcesadorMatcher(RepositorioPlantilla repositorioPlantilla, BlockingQueue<ImagenAnalizar> colaEntrada, BlockingQueue<ImagenAnalizar> colaSalida) {
        this.colaEntrada = colaEntrada;
        this.colaSalida = colaSalida;
        this.repositorioPlantilla = repositorioPlantilla;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ImagenAnalizar imagenAnalizar = colaEntrada.take();
                if (imagenAnalizar.getIdImagen().equals("FIN")) {
                    colaSalida.put(imagenAnalizar);
                    break;
                }

                System.out.println("Procesando matrices y buscando coincidencias en " + imagenAnalizar.getIdImagen());
                final PlantillaPersonaje plantilla=this.repositorioPlantilla.getPlantillas().get(0);
                // se busca
                Mat result =new Mat();
                try {
                    final Mat grande = imagenAnalizar.getImagen();
                    Mat caracter = plantilla.getImagen();
                    Imgproc.matchTemplate(grande, caracter, result, Imgproc.TM_CCOEFF_NORMED);

                    // 5. Encontrar la localización con el valor máximo/mínimo de coincidencia
                    Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                    double mejorScore = mmr.maxVal; // El valor estará entre -1.0 y 1.0 (si usa CCOEFF_NORMED)
                    ResultadoComparar r=new ResultadoComparar(plantilla, imagenAnalizar, mejorScore);
                    Double valor = r.getValor();
                    String nombre = r.getPlantilla().getNombre();
                    imagenAnalizar.setResultado(nombre, (int) (valor * 100));

                    colaSalida.put(imagenAnalizar);
                   /* opencv_imgproc.matchTemplate(
                            src,                                   // Imagen grande donde buscar
                            imagen,                // La cara de referencia del dibujo animado
                            result,                                // La matriz donde OpenCV anotará las notas de parecido
                            opencv_imgproc.CV_TM_CCOEFF_NORMED     // El algoritmo matemático de normalización (resultado entre 0 y 1)
                    );*/
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
               /* System.out.println("se ha anaizado");
                DoublePointer minVal = new DoublePointer(1);
                DoublePointer maxVal = new DoublePointer(1);
                Point minLoc = new Point();
                Point maxLoc = new Point();
                opencv_core.minMaxLoc(result,

                        minVal, // Variable de salida para el valor mínimo (se pasa null si no interesa el peor resultado), es un PUNTERO
                        maxVal,// idem con valor maximo
                        minLoc,
                        maxLoc,
                        null //// Máscara opcional (al pasar null, OpenCV analiza la matriz 'result' por completo)
                );
                double mejorScore = mmr.maxVal.get(0); // El valor estará entre -1.0 y 1.0 (si usa CCOEFF_NORMED)
                ResultadoComparar r=new ResultadoComparar(plantilla, imagenAnalizar, mejorScore);
                Double valor = r.getValor();
                String nombre = r.getPlantilla().getNombre();
                imagenAnalizar.setResultado(nombre, (int) (valor * 100));
                colaSalida.put(imagenAnalizar);*/
                /*Optional<ResultadoComparar> r = this.repositorioPlantilla.getPlantillas().stream().map(plantilla -> {
                    Mat result = null;
                    System.out.println(plantilla.toString());
                    opencv_imgproc.matchTemplate(
                            src,                                   // Imagen grande donde buscar
                            plantilla.getImagen(),                // La cara de referencia del dibujo animado
                            result,                                // La matriz donde OpenCV anotará las notas de parecido
                            opencv_imgproc.CV_TM_CCOEFF_NORMED     // El algoritmo matemático de normalización (resultado entre 0 y 1)
                    );
                    System.out.println("se ha anaizado");
                    DoublePointer minVal = new DoublePointer(1);
                    DoublePointer maxVal = new DoublePointer(1);
                    Point minLoc = new Point();
                    Point maxLoc = new Point();
                    opencv_core.minMaxLoc(result,

                            minVal, // Variable de salida para el valor mínimo (se pasa null si no interesa el peor resultado), es un PUNTERO
                            maxVal,// idem con valor maximo
                            minLoc,
                            maxLoc,
                            null //// Máscara opcional (al pasar null, OpenCV analiza la matriz 'result' por completo)
                    );
                    double mejorScore = maxVal.get(0); // El valor estará entre -1.0 y 1.0 (si usa CCOEFF_NORMED)
                    return new ResultadoComparar(plantilla, imagenAnalizar, mejorScore);

                }).max(Comparator.comparingDouble(a -> a.valor));
                //si se ha encontrdo, siempre lo hará, ya que es valor maximo, se pasa al siguiente hilo
                if (r.isPresent()) {
                    Double valor = r.get().getValor();
                    String nombre = r.get().getPlantilla().getNombre();
                    imagenAnalizar.setResultado(nombre, (int) (valor * 100));
                    System.out.println(imagenAnalizar.getPersonajeDetectado()+" "+imagenAnalizar.getNivelCoincidencia());
                    colaSalida.put(imagenAnalizar);
                }*/


                // Simulación del resultado del matching
                /*if (imagenAnalizar.getIdImagen().contains("homer")) {
                    imagenAnalizar.setResultado("Homer Simpson", 0.92);
                } else {
                    imagenAnalizar.setResultado("Bart Simpson", 0.85);
                }

                colaSalida.put(imagenAnalizar);*/
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}