package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.*;
import com.mycompany.puzzles.InterfacesDAO.InterfazDAO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import jakarta.json.*;

public class InterfazJSON implements InterfazDAO {

    private File fichero = new File("src/main/resources/Ficheros/usuarios.json");
    private File ficheroBloq = new File("src/main/resources/Ficheros/usuariosBloqueados.json");

    @Override
    public boolean lleno() {

        long TAM_MAX = 5L * 1024 * 1024 * 1024; // 5 GB en bytes

        return fichero.length() >= TAM_MAX;

    }

    @Override
    public boolean vacio() {

        if (fichero.exists()) {

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

        Usuario usuario = (Usuario) obj;

        if (!vacio()) {
            List<Usuario> usuarioExiste = buscar();
            for (Usuario u : usuarioExiste) {
                if (u.getEmail().equals(usuario.getEmail())) {
                    throw new DuplicateEntry("El usuario ya existe");
                }
            }
        }

        if (lleno()) {
            throw new DataFullException("No hay espacio en el fichero");
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
            throw new InsercionException("Error al insertar" + e);
        }

    }

    @Override
    public boolean eliminar(String email) throws DataEmptyAccess, DeleteException, DataAccessException, ObjectNotExist {

        if (email == null || email.isEmpty()) {
            throw new DataEmptyAccess("El email está vacío");
        }

        List<Usuario> usuarios = buscar(); // Cargar todos los usuarios desde el fichero
        boolean eliminado = false;

        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                try {

                    String content = new String(Files.readAllBytes(fichero.toPath()), StandardCharsets.UTF_8);

                    // Parsear el JSON como un array
                    JsonReader reader = Json.createReader(new StringReader(content));
                    JsonArray jsonArray = reader.readArray();

                    // Crear un nuevo array sin el usuario que tenga ese email
                    JsonArrayBuilder nuevoArray = Json.createArrayBuilder();
                    for (JsonValue value : jsonArray) {
                        JsonObject usuario = value.asJsonObject();
                        String emails = usuario.getString("email", "");
                        if (!emails.equalsIgnoreCase(email)) {
                            nuevoArray.add(usuario);
                        }
                    }

                    // Escribir el nuevo array al archivo (sobrescribiendo el anterior)
                    try (FileWriter fileWriter = new FileWriter(fichero, false);
                         JsonWriter jsonWriter = Json.createWriter(fileWriter)) {
                        jsonWriter.writeArray(nuevoArray.build());
                    } catch (Exception e) {
                        throw new DeleteException("Error al eliminar" + e);
                    }

                    eliminado = true;
                    System.out.println("Usuario eliminado correctamente.");

                    break; // salimos del while
                } catch (IOException e) {
                    throw new DataAccessException("Error al leer o escribir el archivo: " + e.getMessage());
                }
            }else{
                throw new ObjectNotExist("No existe el usuario a eliminar");
            }
        }

        if (!eliminado) {
            throw new ObjectNotExist("El usuario no existe o la contraseña no coincide.");
        }

        return true;

    }

    @Override
    public boolean actualizar(Object obj) throws ObjectNotExist, DataEmptyAccess {

        if (obj == null) {
            throw new DataEmptyAccess("El objeto usuario está vacío");
        }

        Usuario usuarioActualizado = (Usuario) obj;
        List<Usuario> usuarios = buscar();

        boolean encontrado = false;
        JsonArrayBuilder nuevoArray = Json.createArrayBuilder();

        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(usuarioActualizado.getEmail())) {
                // Sustituimos el antiguo por el nuevo (actualizado)
                nuevoArray.add(usuarioActualizado.toJson());
                encontrado = true;
            } else {
                // Conservamos los demás
                nuevoArray.add(u.toJson());
            }
        }

        if (!encontrado) {
            throw new ObjectNotExist("El usuario no existe en el archivo JSON");
        }

        // Escribimos el JSON actualizado
        try (FileWriter fw = new FileWriter(fichero, false);
             JsonWriter writer = Json.createWriter(fw)) {
            writer.writeArray(nuevoArray.build());
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir el archivo: " + e.getMessage());
        }

        return true;

    }

    @Override
    public List<Usuario> buscar() throws DataEmptyAccess {

        List<Usuario> listaUsuarios = new ArrayList<>();
        if (vacio()){
            throw new DataEmptyAccess("No hay datos");
        }

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
            throw new DataEmptyAccess("Error leyendo el archivo JSON", e);
        }

        return listaUsuarios;

    }

    @Override
    public List<String> buscarAtributo(String atributo) throws DataEmptyAccess {

        List<Usuario> listaUsuarios = buscar();
        List<String> encontrado = new  ArrayList<>();

        if(listaUsuarios.isEmpty()){
            throw new DataEmptyAccess("No hay datos");
        }
        String caracteristica = "";
        for (Usuario usuario : listaUsuarios) {
            JsonObject user = usuario.toJson();
            JsonArray puzzles = user.getJsonArray("puzzles");

            for(int i = 0; i < puzzles.size(); i++){
                JsonObject puzzle = puzzles.getJsonObject(i);

                switch (atributo) {
                    case "autor":
                        caracteristica = puzzle.getString("autor", null);
                        encontrado.add(caracteristica);
                        break;

                    case "media":
                        caracteristica = String.valueOf(puzzle.getInt("media"));
                        encontrado.add(caracteristica);
                        break;

                    case "piezas":
                        caracteristica = String.valueOf(puzzle.getInt("piezas"));
                        encontrado.add(caracteristica);
                        break;

                    case "dificultad":
                        caracteristica = puzzle.getString("dificultad", null);
                        encontrado.add(caracteristica);
                        break;

                    case "valoracion":
                        caracteristica = String.valueOf(puzzle.getInt("valoracion"));
                        encontrado.add(caracteristica);
                        break;
                }

            }
        }
        Collections.sort(encontrado);
        return encontrado;
    }

    @Override
    public boolean bloquearUsuario(Object obj) throws DataFullException, DuplicateEntry,InsercionException {

        Usuario usuario = (Usuario) obj;
        List<Usuario>  listaUsuarios = buscar();

        if (lleno()) {
            throw new DataFullException("No hay espacio en el fichero");
        }
        for(Usuario usuarioAux : listaUsuarios){
            if(usuario.getEmail().equalsIgnoreCase(usuarioAux.getEmail())){
                try{
                    eliminar(usuario.getEmail());
                } catch (DeleteException e) {
                    throw new RuntimeException(e);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        JsonArrayBuilder puzzlesArrayBuilder = Json.createArrayBuilder();
        JsonObject jsonUsuario = Json.createObjectBuilder()
                .add("nombre", usuario.getNombre())
                .add("apellido", usuario.getApellido())
                .add("email", usuario.getEmail())
                .add("passwd", usuario.getPasswd())
                .add("tipo", String.valueOf(Usuario.TipoUsuario.Usuario))
                .add("puzzles", puzzlesArrayBuilder)
                .build();

        try (FileWriter fileWriter = new FileWriter(ficheroBloq.getAbsolutePath(), true);
             JsonWriter jsonWriter = Json.createWriter(fileWriter)) {

            if (vacio()) {

                fileWriter.write("[");
                jsonWriter.writeObject(jsonUsuario);
                fileWriter.write("]");

            } else {

                String content = new String(Files.readAllBytes(ficheroBloq.toPath()), StandardCharsets.UTF_8);

                if (content.trim().endsWith("]")) {
                    int lastIndex = content.lastIndexOf("]");
                    content = content.substring(0, lastIndex);
                }

                if (content.trim().length() > 1) {
                    content += ",";
                }

                Files.write(ficheroBloq.toPath(), content.getBytes(StandardCharsets.UTF_8));

                jsonWriter.writeObject(jsonUsuario);
                fileWriter.write("]");

            }

        }catch (IOException e) {
            throw new InsercionException("Error al insertar" + e);
        }


            return true;
    } // añadir duplicate exception

    @Override
    public Puzzle[] getTopFive() throws DataEmptyAccess {

        List<Usuario> listaUsuarios = buscar();
        List<Puzzle> puzzle = new ArrayList<>();
        List<Float> medias = new ArrayList<>();

        for (Usuario usuarioAux : listaUsuarios) {
            puzzle.addAll(usuarioAux.getPuzzles());
        }

        for (Puzzle puzzleAux : puzzle) {
            medias.add(puzzleAux.getMedia());
        }

        // Ordenar de mayor a menor por media
        puzzle.sort(Comparator.comparingDouble(Puzzle::getMedia).reversed());

        // Crear un array con los 5 primeros (o menos si hay menos de 5)
        int top = Math.min(5, puzzle.size());
        Puzzle[] arrayPuzzle = new Puzzle[top];

        for (int i = 0; i < top; i++) {
            arrayPuzzle[i] = puzzle.get(i);
        }

        return arrayPuzzle;
    }

    @Override
    public String mejorTiempo() {
        return "";
    }

}