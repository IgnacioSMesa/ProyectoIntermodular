package com.mycompany.puzzles.Clases;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import java.util.List;

public class Usuario {

    public enum TipoUsuario {Admin, Bloqueado, Usuario}

    private String nombre;
    private String apellido;
    private String email;
    private String passwd;
    private TipoUsuario tipoUsuario;
    private List<Puzzle> puzzles;

    //Constructores
    public Usuario() {}

    public Usuario(String nombre, String apellido, String email, String passwd, TipoUsuario tipoUsuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwd = passwd;
        this.tipoUsuario = tipoUsuario;
    }

    public Usuario(String nombre, String apellido, String email, String passwd, TipoUsuario tipoUsuario, List<Puzzle> puzzles) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.passwd = passwd;
        this.tipoUsuario = tipoUsuario;
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

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
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

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
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
                ", tipoUsuario=" + tipoUsuario +
                ", puzzles=" + puzzles +
                '}';
    }
    public JsonObject toJson() {
        JsonArrayBuilder puzzlesArray = Json.createArrayBuilder();
        for (Puzzle p : puzzles) {
            puzzlesArray.add(Json.createObjectBuilder()
                    .add("autor", p.getAutor())
                    .add("media", p.getMedia())
                    .add("piezas", p.getPiezas())
                    .add("dificultad", p.getDificultad().toString())
                    .add("descripcion", p.getDescripcion())
                    .add("color", p.isColor())
                    .add("valoracion", p.getValoracion()));
        }

        return Json.createObjectBuilder()
                .add("nombre", nombre)
                .add("apellido", apellido)
                .add("email", email)
                .add("passwd", passwd)
                .add("tipo", tipoUsuario.toString())
                .add("puzzles", puzzlesArray)
                .build();
    }


}