package ies.sequeros.dam.psp.threads.patrones.pipeline;

import org.bytedeco.opencv.opencv_core.Mat;

public class Personaje {

        private final String idImagen;
        private String personajeDetectado;
        private Mat imagen;
        public Personaje(String idImagen, String personajeDetectado) {
            this.idImagen = idImagen;
            this.personajeDetectado = personajeDetectado;
        }
        private double nivelCoincidencia;

        public Personaje(String idImagen) {
            this.idImagen = idImagen;

        }

        public String getIdImagen() { return idImagen; }
        public void setResultado(String personaje, double score) {
            this.personajeDetectado = personaje;
            this.nivelCoincidencia = score;
        }
        public void setImagen(Mat imagen) {
            this.imagen = imagen;
        }
        public Mat getImagen() {
            return imagen;
        }
        public double getNivelCoincidencia() { return nivelCoincidencia; }
        public String getPersonajeDetectado() { return personajeDetectado; }
    }

