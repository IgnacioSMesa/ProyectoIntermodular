package com.mycompany.puzzles.Clases;

import java.util.List;

public class Usuario {

    private String nombre;
    private String apellido;
    private String email;
    private String passwd;
    private Boolean esAdmin;
    private List<Puzzle> puzzles;

    //Constructores
    public Usuario() {}

    public Usuario(String nombre, String apellido, String email, String passwd, Boolean esAdmin) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwd = passwd;
        this.esAdmin = esAdmin;
    }

    public Usuario(String nombre, String apellido, String email, String passwd, Boolean esAdmin, List<Puzzle> puzzles) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwd = passwd;
        this.esAdmin = esAdmin;
        this.puzzles = puzzles;
    }

    //Getters
    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswd() {
        return passwd;
    }

    public Boolean getEsAdmin() {
        return esAdmin;
    }

    public List<Puzzle> getPuzzles() {
        return puzzles;
    }

    //Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setEsAdmin(Boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public void setPuzzles(List<Puzzle> puzzles) {
        this.puzzles = puzzles;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", passwd='" + passwd + '\'' +
                ", esAdmin=" + esAdmin +
                ", puzzles=" + puzzles +
                '}';
    }

}