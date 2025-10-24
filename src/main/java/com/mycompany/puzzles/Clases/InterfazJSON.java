package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.*;
import com.mycompany.puzzles.InterfacesDAO.InterfazDAO;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;

public class InterfazJSON implements InterfazDAO {
    @Override
    public boolean lleno() {
        return false;
    }

    @Override
    public boolean vacio() {
        return false;
    }

    @Override
    public boolean insertar(Object obj) throws InsercionException, DataFullException, DuplicateEntry {

        Usuario usuario = (Usuario) obj;

/// Construir el objeto JSON usando JSON-P

        JsonObject jsonUsuario = Json.createObjectBuilder()
                .add("nombre", usuario.getNombre())
                .add("apellido", usuario.getApellido())
                .add("email", usuario.getEmail())
                .add("passwd", usuario.getPasswd())
                .add("tipo", String.valueOf(Usuario.TipoUsuario.USUARIO))
                .add("puzzles", String.valueOf(usuario.getPuzzles()))
                .build();
/// Escribir el objeto JSON en un archivo
        try (FileWriter fileWriter = new FileWriter("C:\\Users\\2DAM\\IdeaProjects\\ProyectoIntermodular\\src\\main\\java\\com\\mycompany\\puzzles\\Ficheros\\usuarios.json");
             JsonWriter jsonWriter = Json.createWriter(fileWriter)) {
            jsonWriter.writeObject(jsonUsuario);
            System.out.println("Se ha escrito el objeto JugadorNFL en el archivo usuarios.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(Object obj) throws ObjectNotExist, DataEmptyAccess {
        return false;
    }

    @Override
    public boolean actualizar(Object obj) throws ObjectNotExist, DataEmptyAccess {
        return false;
    }

    @Override
    public void buscar() throws DataEmptyAccess {
        try (FileReader fileReader = new FileReader("C:\\Users\\2DAM\\IdeaProjects\\ProyectoIntermodular\\src\\main\\java\\com\\mycompany\\puzzles\\Ficheros\\usuarios.json");
             JsonReader jsonReader = Json.createReader(fileReader)) {
            JsonObject jsonUsuario = jsonReader.readObject();
/// Crear el objeto JugadorNFL a partir del JSON
            Usuario usuario = new Usuario(/*
                    jsonUsuario.getString("nombre"),
                    jsonUsuario.getString("apellido"),
                    jsonUsuario.getString("email"),
                    jsonUsuario.getString("passwd"),
                    (Usuario.TipoUsuario) jsonUsuario.getString("tipo"),
                    jsonUsuario.getString("puzzles")*/
                     );
/// Mostrar el objeto leído
            System.out.println("Jugador leído del archivo: " + usuario);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void buscarAtributo(String atributo) throws DataEmptyAccess {

    }

    @Override
    public boolean bloquearUsuario(Object obj) throws DataFullException, DuplicateEntry {
        return false;
    }

    @Override
    public List<Object> getTopFive(Object obj) throws DataEmptyAccess {
        return List.of();
    }

    @Override
    public String mejorTiempo() {
        return "";
    }

}