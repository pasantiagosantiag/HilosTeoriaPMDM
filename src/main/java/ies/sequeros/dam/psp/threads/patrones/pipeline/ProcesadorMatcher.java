package ies.sequeros.dam.psp.threads.patrones.pipeline;



import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Comparator;
import java.util.List;
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
                //se toma la siguiente imagen a analizar
                ImagenAnalizar imagenAnalizar = colaEntrada.take();
                //si tiene el nombre de fin, se sale del bucle
                if (analizarImagen(imagenAnalizar)) break;

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean analizarImagen(ImagenAnalizar imagenAnalizar) throws InterruptedException {
        if (imagenAnalizar.getIdImagen().equals("FIN")) {
            colaSalida.put(imagenAnalizar);
            return true;
        }
        final Mat grande = imagenAnalizar.getImagen();
        final Mat grandeGris;
        //se convierte a gris en caso de estar en color
        if (grande.channels() == 3 || grande.channels() == 4) {

            // Solo si tiene 3 o 4 canales (BGR/BGRA) convertimos a escala de grises
            grandeGris = new Mat();
            Imgproc.cvtColor(grande, grandeGris, Imgproc.COLOR_BGR2GRAY);
        } else {
            // Si ya tiene 1 canal (ya es escala de grises), clonamos la matriz directamente
            grandeGris = grande.clone();
        }

        List<PlantillaPersonaje> plantillas = this.repositorioPlantilla.getPlantillas();
        Optional<ResultadoComparar> r = buscarPersonaje(imagenAnalizar, plantillas, grande, grandeGris);
        if (r.isPresent()) {
            Double valor = r.get().getValor();
            String nombre = r.get().getPlantilla().getNombre();
            imagenAnalizar.setResultado(nombre, valor );
           // System.out.println(imagenAnalizar.getPersonajeDetectado()+" "+imagenAnalizar.getNivelCoincidencia());
            colaSalida.put(imagenAnalizar);
        }
        return false;
    }

    private Optional<ResultadoComparar> buscarPersonaje(ImagenAnalizar imagenAnalizar, List<PlantillaPersonaje> plantillas, Mat grande, Mat grandeGris) {
        //se busca el mayor observar que el stream es paralelo
        Optional<ResultadoComparar> r = plantillas.stream().parallel().map(plantilla -> {
            Mat caracter = plantilla.getImagen();

            //  Validar que la plantilla no sea mayor que la imagen origen
            if (caracter.cols() > grande.cols() || caracter.rows() > grande.rows()) {
                return new ResultadoComparar(plantilla, imagenAnalizar, -1.0);
            }

            //  Validar que ambas imágenes tengan el mismo tipo/canales
            if (caracter.type() != grandeGris.type()) {
                return new ResultadoComparar(plantilla, imagenAnalizar, -1.0);
            }
            Mat result = new Mat();
            double mejorScore=-1.0;
            try {
                Imgproc.matchTemplate(grandeGris, caracter, result, Imgproc.TM_CCOEFF_NORMED);
                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                mejorScore = mmr.maxVal;
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                result.release();
            }
            return new ResultadoComparar(plantilla, imagenAnalizar, mejorScore);
        }).max(Comparator.comparingDouble(a -> a.valor));
        return r;
    }
}