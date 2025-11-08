package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.ArgumentException;

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

    public Puzzle(String autor, int tiempo, int piezas, Dificultades dificultad, String descripcion, boolean color, int valoracion) throws ArgumentException {
        try{
            setAutor(autor);
            setTiempo(tiempo);
            setPiezas(piezas);
            setDificultad(dificultad);
            setDescripcion(descripcion);
            setColor(color);
            setValoracion(valoracion);

        }catch(Exception e){
            throw new ArgumentException(e.getMessage());
        }
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
    public void setAutor(String autor) throws ArgumentException {
        if (autor == null) {
            throw new ArgumentException("Autor no puede ser nulo");
        }
        this.autor = autor;
    }

    public void setTiempo(int tiempo)throws ArgumentException {
        if (tiempo < 0) {
            throw new ArgumentException("Tiempo no puede ser negativo");
        }
        this.tiempo = tiempo;
    }

    public void setPiezas(int piezas)throws ArgumentException {
        if (piezas < 500) {
            throw new ArgumentException("El mínimo de piezas son 500 en adelante");
        }
        this.piezas = piezas;
    }

    public void setDificultad(Dificultades dificultad)throws ArgumentException {
        if (dificultad == null) {
            throw new ArgumentException("Dificultad no puede ser nulo");
        }
        if (!dificultad.equals(Dificultades.Facil) ||!dificultad.equals(Dificultades.Medio) ||!dificultad.equals(Dificultades.Extremo)) {
            throw new ArgumentException("Dificultad no encontrado");
        }
        this.dificultad = dificultad;
    }

    public void setDescripcion(String descripcion)throws ArgumentException {
        if (descripcion == null) {
            throw new ArgumentException("Descripcion no puede ser nulo");
        }
        this.descripcion = descripcion;
    }

    public void setColor(boolean color)throws ArgumentException {
        if(color!=true || color!=false){
            throw new ArgumentException("El color solo puede ser true o false");
        }
        this.color = color;
    }

    public void setValoracion(int valoracion)throws ArgumentException {
        if (valoracion < 0 || valoracion > 5) {
            throw new ArgumentException("Valoracion tiene que ser entre 0 y 5");
        }
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