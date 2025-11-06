package com.mycompany.puzzles.Clases;

public class Puzzle {

    public enum Dificultades {Facil, Medio, Dificil, Extremo};

    private String autor;
    private int tiempo;
    private int piezas;
    private Dificultades dificultad;
    private String descripcion;
    private boolean color;//color unico o no
    private int valoracion;

    //Constructores
    public Puzzle() {}

    public Puzzle(String autor, int tiempo, int piezas, Dificultades dificultad, String descripcion, boolean color, int valoracion) {
        this.autor = autor;
        this.tiempo = tiempo;
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

    public int getTiempo() {
        return tiempo;
    }

    public int getPiezas() {
        return piezas;
    }

    public Dificultades getDificultad() {
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

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public void setPiezas(int piezas) {
        this.piezas = piezas;
    }

    public void setDificultad(Dificultades dificultad) {
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
        return String.format(
                "🧩 Puzzle\n" +
                        " ├ Autor: %s\n" +
                        " ├ Tiempo: %d horas\n" +
                        " ├ Piezas: %d\n" +
                        " ├ Dificultad: %s\n" +
                        " ├ Descripción: %s\n" +
                        " ├ Color: %s\n" +
                        " └ Valoración: %d/5",
                autor,
                tiempo,
                piezas,
                dificultad,
                descripcion,
                color ? "Sí" : "No",
                valoracion
        );
    }

}