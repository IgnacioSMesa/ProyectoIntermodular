package com.mycompany.puzzles.Clases;

public class Puzzle {

    public enum dificultades {FACIL, MEDIO, DIFICIL, EXTREMO};

    private String autor;
    private float media;
    private int piezas;
    private dificultades dificultad;
    private String descripcion;
    private boolean color;//color unico o no
    private int valoracion;

    //Constructores
    public Puzzle() {}

    public Puzzle(String autor, float media, int piezas, dificultades dificultad, String descripcion, boolean color, int valoracion) {
        this.autor = autor;
        this.media = media;
        this.piezas = piezas;
        this.dificultad = dificultad;
        this.descripcion = descripcion;
        this.color = color;
        this.valoracion = valoracion;
    }

    //Getters
    public String getAutor() {
        return autor;
    }

    public float getMedia() {
        return media;
    }

    public int getPiezas() {
        return piezas;
    }

    public dificultades getDificultad() {
        return dificultad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isColor() {
        return color;
    }

    public int getValoracion() {
        return valoracion;
    }

    //Setters
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setMedia(float media) {
        this.media = media;
    }

    public void setPiezas(int piezas) {
        this.piezas = piezas;
    }

    public void setDificultad(dificultades dificultad) {
        this.dificultad = dificultad;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    @Override
    public String toString() {
        return "Puzzle{" +
                "autor='" + autor + '\'' +
                ", media=" + media +
                ", piezas=" + piezas +
                ", dificultad=" + dificultad +
                ", descripcion='" + descripcion + '\'' +
                ", color=" + color +
                ", valoracion=" + valoracion +
                '}';
    }

}