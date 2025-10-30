package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.*;
import com.mycompany.puzzles.InterfacesDAO.InterfazDAO;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import jakarta.json.*;

public class InterfazJSON implements InterfazDAO {

    // private File fichero = new File("C:\\Users\\natal\\OneDrive\\Documentos\\NetBeansProjects\\ProyectoIntermodular\\src\\main\\java\\com\\mycompany\\puzzles\\Ficheros\\usuarios.json");
    private File fichero = new File("src/main/resources/Ficheros/usuarios.json");

    @Override
    public boolean lleno() {
        return false;
    }

    @Override
    public boolean vacio() {

        if (fichero.exists()) {

            System.out.println(fichero.length());

            if (fichero.length() == 0) {
                return true;
            } else  {
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public boolean insertar(Object obj) throws InsercionException, DataFullException, DuplicateEntry {
        System.out.println(fichero);
        Usuario usuario = (Usuario) obj;

        if (!vacio()) {
            List<Usuario> usuarioExiste = buscar();
            for (Usuario u : usuarioExiste) {
                if (u.getEmail().equals(usuario.getEmail())) {
                    throw new InsercionException("El usuario ya existe");
                }
            }
        }

        JsonArrayBuilder puzzlesArrayBuilder = Json.createArrayBuilder();
        for (Puzzle p : usuario.getPuzzles()) {
            JsonObject jsonPuzzle = Json.createObjectBuilder()
                    .add("autor", p.getAutor())
                    .add("media", p.getMedia())
                    .add("piezas", p.getPiezas())
                    .add("dificultad", p.getDificultad().toString())
                    .add("descripcion", p.getDescripcion())
                    .add("color", p.isColor())
                    .add("valoracion", p.getValoracion())
                    .build();
            puzzlesArrayBuilder.add(jsonPuzzle);
        }

        JsonObject jsonUsuario = Json.createObjectBuilder()
                .add("nombre", usuario.getNombre())
                .add("apellido", usuario.getApellido())
                .add("email", usuario.getEmail())
                .add("passwd", usuario.getPasswd())
                .add("tipo", String.valueOf(Usuario.TipoUsuario.Usuario))
                .add("puzzles", puzzlesArrayBuilder)
                .build();

        try (FileWriter fileWriter = new FileWriter(fichero.getAbsolutePath(), true);
             JsonWriter jsonWriter = Json.createWriter(fileWriter)) {

            if (vacio()) {

                fileWriter.write("[");
                jsonWriter.writeObject(jsonUsuario);
                fileWriter.write("]");

            } else {

                String content = new String(Files.readAllBytes(fichero.toPath()), StandardCharsets.UTF_8);

                if (content.trim().endsWith("]")) {
                    int lastIndex = content.lastIndexOf("]");
                    content = content.substring(0, lastIndex);
                }

                if (content.trim().length() > 1) {
                    content += ",";
                }

                Files.write(fichero.toPath(), content.getBytes(StandardCharsets.UTF_8));

                jsonWriter.writeObject(jsonUsuario);
                fileWriter.write("]");

            }

            return true;

        } catch (IOException e) {
            System.out.println("No se pudo insertar el usuario: " + e.getMessage());
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
    public List<Usuario> buscar() throws DataEmptyAccess {

        List<Usuario> listaUsuarios = new ArrayList<>();

        try (FileReader fileReader = new FileReader(fichero);
             JsonReader jsonReader = Json.createReader(fileReader)) {

            JsonArray jsonUsuarios = jsonReader.readArray();

            for (JsonValue jsonValue : jsonUsuarios) {

                JsonObject jsonUsuario = jsonValue.asJsonObject();
                JsonArray jsonPuzzles = jsonUsuario.getJsonArray("puzzles");

                List<Puzzle> puzzles = new ArrayList<>();

                for (JsonValue puzzleValue : jsonPuzzles) {

                    JsonObject jsonPuzzle = puzzleValue.asJsonObject();

                    Puzzle p = new Puzzle();

                    p.setAutor(jsonPuzzle.getString("autor"));
                    p.setMedia((float) jsonPuzzle.getJsonNumber("media").doubleValue());
                    p.setPiezas(jsonPuzzle.getInt("piezas", 0));
                    p.setDificultad(Puzzle.Dificultades.valueOf(jsonPuzzle.getString("dificultad")));
                    p.setDescripcion(jsonPuzzle.getString("descripcion"));
                    p.setColor(jsonPuzzle.getBoolean("color"));
                    p.setValoracion(jsonPuzzle.getInt("valoracion", 0));

                    puzzles.add(p);

                }

                Usuario usuario = new Usuario(
                        jsonUsuario.getString("nombre"),
                        jsonUsuario.getString("apellido"),
                        jsonUsuario.getString("email"),
                        jsonUsuario.getString("passwd"),
                        Usuario.TipoUsuario.valueOf(jsonUsuario.getString("tipo")),
                        puzzles
                );

                listaUsuarios.add(usuario);

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new DataEmptyAccess("Error leyendo el archivo JSON", e);
        }

        return listaUsuarios;

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