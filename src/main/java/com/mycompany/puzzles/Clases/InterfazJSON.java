package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.*;
import com.mycompany.puzzles.InterfacesDAO.InterfazDAO;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import jakarta.json.JsonReader;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InterfazJSON implements InterfazDAO {

    // Ficheros de destino
    private File fichero = new File("src/main/resources/Ficheros/usuarios.json");
    private File ficheroBloq = new File("src/main/resources/Ficheros/usuariosBloqueados.json");

    @Override
    public boolean lleno() {
        // Establecemos un tamaño máximo para el fichero
        long TAM_MAX = 5L * 1024 * 1024 * 1024; // 5 GB en bytes

        // Retorna true si el tamaño del fichero supera al máximo establecido
        return fichero.length() >= TAM_MAX;

    }

    @Override
    public boolean vacio() {

        if (fichero.exists()) {
            // Retorna true si el fichero no tiene nada
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
        // Casteamos el obj a un objeto Usuario
        Usuario usuario = (Usuario) obj;

        // Comprobamos que el usuario a insertar no este ya creado, nosotros lo hacemos con el campo email
        if (!vacio()) {
            List<Usuario> usuarioExiste = buscar();
            for (Usuario u : usuarioExiste) {
                if (u.getEmail().equals(usuario.getEmail())) {
                    throw new DuplicateEntry("El usuario ya existe");
                }
            }
        }
        // Comprobar si esta lleno
        if (lleno()) {
            throw new DataFullException("No hay espacio en el fichero");
        }


        // Construimos un array JSON para los puzzles del usuario
        JsonArrayBuilder puzzlesArrayBuilder = Json.createArrayBuilder();

        for (Puzzle p : usuario.getPuzzles()) {
            // Para cada puzzle, creamos un objeto JSON con todos sus campos
            JsonObject jsonPuzzle = Json.createObjectBuilder()
                    .add("autor", p.getAutor())
                    .add("tiempo", p.getTiempo())
                    .add("piezas", p.getPiezas())
                    .add("dificultad", p.getDificultad().toString())
                    .add("descripcion", p.getDescripcion())
                    .add("color", p.isColor())
                    .add("valoracion", p.getValoracion())
                    .build();

            // Añadimos el puzzle al array JSON
            puzzlesArrayBuilder.add(jsonPuzzle);
        }

        // Creamos el objeto JSON principal del usuario
        JsonObject jsonUsuario = Json.createObjectBuilder()
                .add("nombre", usuario.getNombre())
                .add("apellido", usuario.getApellido())
                .add("email", usuario.getEmail())
                .add("passwd", usuario.getPasswd())
                .add("tipo", String.valueOf(Usuario.TipoUsuario.Usuario))
                // Añadimos la lista de puzzles creada arriba
                .add("puzzles", puzzlesArrayBuilder)
                .build();

        try (
                // Abrimos el FileWriter en modo append (añadir al final)
                FileWriter fileWriter = new FileWriter(fichero.getAbsolutePath(), true);
                // JsonWriter usará ese FileWriter para escribir JSON
                JsonWriter jsonWriter = Json.createWriter(fileWriter)
        ) {

            // Si el fichero está vacío, estamos creando un JSON nuevo
            if (vacio()) {

                // Abrimos un array JSON
                fileWriter.write("[");
                // Escribimos el usuario como primer elemento
                jsonWriter.writeObject(jsonUsuario);
                // Cerramos el array JSON
                fileWriter.write("]");

            } else {

                // Leemos el contenido actual del archivo JSON
                String content = new String(Files.readAllBytes(fichero.toPath()), StandardCharsets.UTF_8);

                // Si el archivo termina en ']' quitamos ese último corchete
                // para poder añadir otro usuario al array
                if (content.trim().endsWith("]")) {
                    int lastIndex = content.lastIndexOf("]");
                    content = content.substring(0, lastIndex);
                }

                // Si el contenido ya tenía elementos antes, añadimos una coma
                if (content.trim().length() > 1) {
                    content += ",";
                }

                // Escribimos el contenido modificado (sin el corchete final)
                Files.write(fichero.toPath(), content.getBytes(StandardCharsets.UTF_8));

                // Añadimos el nuevo usuario
                jsonWriter.writeObject(jsonUsuario);

                // Cerramos de nuevo el array JSON
                fileWriter.write("]");
            }

            return true;

        } catch (IOException e) {

            // Cualquier error de IO se encapsula en tu excepción personalizada
            throw new InsercionException("Error al insertar" + e);
        }

    }

    @Override
    public boolean eliminar(String email) throws DataEmptyAccess, DeleteException, DataAccessException, ObjectNotExist {

        // Comprobación de argumentos
        if (email == null || email.isEmpty()) {
            throw new DataEmptyAccess("El email está vacío");
        }

        List<Usuario> usuarios = buscar(); // Cargar todos los usuarios desde el fichero
        boolean eliminado = false; // Variable que nos dirá si se ha eliminado


        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {

                try {
                    // Leemos el contenido
                    String content = new String(Files.readAllBytes(fichero.toPath()), StandardCharsets.UTF_8);

                    // Creamos el Json reader y declaramos el objeto json array
                    JsonReader reader = Json.createReader(new StringReader(content));
                    JsonArray jsonArray = reader.readArray();

                    JsonArrayBuilder nuevoArray = Json.createArrayBuilder();

                    // Leemos el array y establecemos un jsonObjetc con el usuario
                    for (JsonValue value : jsonArray) {
                        JsonObject usuario = value.asJsonObject();
                        String emails = usuario.getString("email", "");
                        if (!emails.equalsIgnoreCase(email)) {
                            nuevoArray.add(usuario);
                        }
                    }

                    // reescribimos el fichero sin el usuario que haya encontrado
                    try (FileWriter fileWriter = new FileWriter(fichero, false);
                         JsonWriter jsonWriter = Json.createWriter(fileWriter)) {
                        jsonWriter.writeArray(nuevoArray.build());
                    }

                    eliminado = true;
                    break;

                } catch (IOException e) {
                    throw new DataAccessException("Error al leer o escribir el archivo: " + e.getMessage());
                }
            }
        }

        if (!eliminado) {
            throw new ObjectNotExist("El usuario no existe o la contraseña no coincide.");
        }

        return true;

    }

    @Override
    public boolean actualizar(Object obj) throws ObjectNotExist, DataEmptyAccess,InsercionException {

        // Comprobación de los argumentos
        if (obj == null) {
            throw new DataEmptyAccess("El objeto usuario está vacío");
        }

        // Cast de usuario y lista de los usuarios
        Usuario usuarioActualizado = (Usuario) obj;
        List<Usuario> usuarios = buscar();

        //Variable que nos dirá si el usuario se ha encontrado y declaramos un JsonArrayBuilder
        boolean encontrado = false;
        JsonArrayBuilder nuevoArray = Json.createArrayBuilder();

        // Recorremos la lsia de usuarios
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
            throw new InsercionException("Error al escribir el archivo: " + e.getMessage());
        }

        return true;

    }

    @Override
    public List<Usuario> buscar() throws DataEmptyAccess {

        // Lista para guaqrdar los usuarios
        List<Usuario> listaUsuarios = new ArrayList<>();

        // Comprobamos si el fichero está vacío
        if (vacio()){
            throw new DataEmptyAccess("No hay datos");
        }

        // Hacemos el FileReader con el fichero de usuarios JSON
        try (FileReader fileReader = new FileReader(fichero);
             JsonReader jsonReader = Json.createReader(fileReader)) {

            // Para guardar lo que encontremos en el json
            JsonArray jsonUsuarios = jsonReader.readArray();

            // Por cada uno vamos creando los objetos necesario, ya sea puzzle o usuario
            for (JsonValue jsonValue : jsonUsuarios) {

                JsonObject jsonUsuario = jsonValue.asJsonObject();
                JsonArray jsonPuzzles = jsonUsuario.getJsonArray("puzzles");

                List<Puzzle> puzzles = new ArrayList<>();

                for (JsonValue puzzleValue : jsonPuzzles) {

                    JsonObject jsonPuzzle = puzzleValue.asJsonObject();

                    Puzzle p = new Puzzle();

                    p.setAutor(jsonPuzzle.getString("autor"));
                    p.setTiempo(jsonPuzzle.getInt("tiempo", 0));
                    p.setPiezas(jsonPuzzle.getInt("piezas", 0));
                    p.setDificultad(Puzzle.Dificultades.valueOf(jsonPuzzle.getString("dificultad")));
                    p.setDescripcion(jsonPuzzle.getString("descripcion"));
                    p.setColor(jsonPuzzle.getBoolean("color"));
                    p.setValoracion(jsonPuzzle.getInt("valoracion", 0));

                    // Agregamos a la lista
                    puzzles.add(p);

                }

                Usuario usuario = new Usuario(
                        jsonUsuario.getString("nombre"),
                        jsonUsuario.getString("apellido"),
                        jsonUsuario.getString("email"),
                        jsonUsuario.getString("passwd"),
                        Usuario.TipoUsuario.valueOf(jsonUsuario.getString("tipo")),
                        puzzles //Lista de puzzles del usuario en caso de tener
                );

                // Agregamos el usuario con puzzles en caso de que tenga
                listaUsuarios.add(usuario);

            }

        } catch (IOException | ArgumentException e) {
            throw new DataEmptyAccess("Error leyendo el archivo JSON", e);
        }

        return listaUsuarios;

    }

    @Override
    public List<String> buscarAtributo(String atributo) throws DataEmptyAccess {

        // Lista de usuarios y lista para meter al que queremos buscar
        List<Usuario> listaUsuarios = buscar();
        List<String> encontrado = new  ArrayList<>();

        // Comprobación de que haya usuarios
        if(listaUsuarios.isEmpty()){
            throw new DataEmptyAccess("No hay datos");
        }

        // Por cada uno de los resultados, según el atributo que queramos nos establecerá la característica
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

                    case "tiempo":
                        caracteristica = String.valueOf(puzzle.getInt("tiempo"));
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
        // Ordenamos la lista y la devolvemos
        Collections.sort(encontrado);
        return encontrado;
    }

    @Override
    public boolean bloquearUsuario(Object obj) throws DataFullException, InsercionException {

        // Cast del obj a usurio y lista de usuarios
        Usuario usuario = (Usuario) obj;
        List<Usuario>  listaUsuarios = buscar();

        if (lleno()) {
            throw new DataFullException("No hay espacio en el fichero");
        }
        // Comprobamos el usuario
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


        // Lo declaramos para que escriba un nuevo objeto json con el usuario a bloquear en el fichero de usuario bloqueados
        JsonArrayBuilder puzzlesArrayBuilder = Json.createArrayBuilder();
        JsonObject jsonUsuario = Json.createObjectBuilder()
                .add("nombre", usuario.getNombre())
                .add("apellido", usuario.getApellido())
                .add("email", usuario.getEmail())
                .add("passwd", usuario.getPasswd())
                .add("tipo", String.valueOf(Usuario.TipoUsuario.Usuario))
                .add("puzzles", puzzlesArrayBuilder)
                .build();

        // Escribimos el usuario, sin puzzles,  en el fichero de usuariosJson de bloqueados
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

        // Lista de usuario, para guardar los puzzles y las medias, ya que ordenamos por media
        List<Usuario> listaUsuarios = buscar();
        List<Puzzle> puzzle = new ArrayList<>();
        List<Integer> medias = new ArrayList<>();

        for (Usuario usuarioAux : listaUsuarios) {
            puzzle.addAll(usuarioAux.getPuzzles());
        }
        // blucle
        for (Puzzle puzzleAux : puzzle) {
            medias.add(puzzleAux.getTiempo());
        }

        // Ordenar de mayor a menor por media
        puzzle.sort(Comparator.comparingDouble(Puzzle::getTiempo).reversed());

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
        List<String> tiempos = buscarAtributo("tiempo");
        List<Integer> tiemposInt = new ArrayList<>();

        for (String tiempo : tiempos) {
            try {
                tiemposInt.add(Integer.parseInt(tiempo)); // convierte cada String a int
            } catch (NumberFormatException e) {
                System.err.println("Valor de tiempo no válido: " + tiempo);
            }
        }

        if (tiemposInt.isEmpty()) {
            return "No hay tiempos disponibles";
        }

        // Ordenar de menor a mayor → el mejor tiempo es el primero
        tiemposInt.sort(Comparator.naturalOrder());
        int mejor = tiemposInt.get(0);

        return String.valueOf(mejor);
    }

}