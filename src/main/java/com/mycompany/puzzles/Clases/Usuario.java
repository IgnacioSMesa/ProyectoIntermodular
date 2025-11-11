package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.ArgumentException;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Usuario(String nombre, String apellido, String email, String passwd, TipoUsuario tipoUsuario) throws ArgumentException {
        try {
            setNombre(nombre);
            setApellido(apellido);
            setEmail(email);
            setPasswd(passwd);
            setTipoUsuario(tipoUsuario);
        }catch (Exception e){
            throw new ArgumentException(e.getMessage());
        }
    }

    public Usuario(String nombre, String apellido, String email, String passwd, TipoUsuario tipoUsuario, List<Puzzle> puzzles) throws ArgumentException {
        try {
            setNombre(nombre);
            setApellido(apellido);
            setEmail(email);
            setPasswd(passwd);
            setTipoUsuario(tipoUsuario);
            setPuzzles(puzzles);
        }catch (Exception e){
            throw new ArgumentException(e.getMessage());
        }
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
    public void setNombre(String nombre)throws ArgumentException {
        if (nombre == null) {
            throw new ArgumentException("El nombre del usuario no puede ser nulo");
        }
        this.nombre = nombre;
    }

    public void setApellido(String apellido)throws ArgumentException {
        if (apellido == null) {
            throw new ArgumentException("El nombre del usuario no puede ser nulo");
        }
        this.apellido = apellido;
    }

    public void setEmail(String email)throws ArgumentException {
        if (email == null || email.isEmpty()) {
            throw new ArgumentException("El email del usuario no puede ser nulo o vacío");
        }

        String regex = "^[A-Za-z0-9._]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new ArgumentException("El email debe tener un formato válido de Gmail (por ejemplo: usuario@gmail.com)");
        }

        this.email = email;
    }

    public void setPasswd(String passwd)throws ArgumentException {

        if (passwd == null || passwd.isEmpty()) {
            throw new ArgumentException("La password del usuario no puede ser nulo");
        }

        if (passwd.length() < 8) {
            throw new ArgumentException("El mínimo debe tener 8 caracteres");
        }
        this.passwd = passwd;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario)throws ArgumentException {
        if (tipoUsuario == null) {
            throw new ArgumentException("El tipo de usuario no puede ser nulo");
        }
        if (!tipoUsuario.equals(TipoUsuario.Admin) && !tipoUsuario.equals(TipoUsuario.Bloqueado) && !tipoUsuario.equals(TipoUsuario.Usuario)) {
            throw new ArgumentException("Tipo de usuario no válido");
        }
        this.tipoUsuario = tipoUsuario;
    }

    public void setPuzzles(List<Puzzle> puzzles)throws ArgumentException {
        if (puzzles == null) {
            throw new ArgumentException("Las puzzles no puede ser nulos");
        }
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
                    .add("tiempo", p.getTiempo())
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