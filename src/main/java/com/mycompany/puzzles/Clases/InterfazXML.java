package com.mycompany.puzzles.Clases;

import com.mycompany.puzzles.Excecpiones.*;
import com.mycompany.puzzles.InterfacesDAO.InterfazDAO;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InterfazXML implements InterfazDAO {

    // Rutas a los ficheros de XML
    private File fichero = new File("src/main/resources/Ficheros/usuarios.xml");
    private File ficheroBloq = new File("src/main/resources/Ficheros/usuariosBloqueados.xml");

    @Override
    public boolean lleno() {

        // Establecemos un tamaño máximo y retornamos true cuando supere el tamaño
        long TAM_MAX = 5L * 1024 * 1024 * 1024; // 5 GB
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

        // Casteamos el obj a un tipo Usuario
        Usuario usuario = (Usuario) obj;

        try {

            // Comprobación de si el fichero está lleno
            if (lleno()) throw new DataFullException("No hay espacio en el fichero");


            // Se crea una fábrica de constructores de documentos XML (DOM)
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // A partir de la fábrica, se obtiene un "builder" que permite construir o leer documentos XML
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc; // Variable que contendrá el documento XML en memoria

            // Si el fichero XML está vacío
            if (vacio()) {
                // Se crea un nuevo documento XML en blanco
                doc = builder.newDocument();
                // Se crea el elemento raíz llamado "usuarios"
                Element root = doc.createElement("usuarios");
                // Se añade el elemento raíz al documento
                doc.appendChild(root);
            } else {
                // Si el fichero ya tiene contenido, se carga (parsea) el documento existente
                doc = builder.parse(fichero);
            }

            // Se obtienen todos los nodos <usuario> existentes en el XML
            NodeList usuarios = doc.getElementsByTagName("usuario");

            // Se recorre cada usuario para comprobar si ya existe uno con el mismo email
            for (int i = 0; i < usuarios.getLength(); i++) {
                Element u = (Element) usuarios.item(i);
                // Si el email del usuario en el XML coincide con el nuevo, se lanza una excepción
                if (u.getElementsByTagName("email").item(0).getTextContent().equalsIgnoreCase(usuario.getEmail())) {
                    throw new DuplicateEntry("El usuario ya existe");
                }
            }

            // Si el email no existe, se crea un nuevo elemento <usuario>
            Element usuarioElem = doc.createElement("usuario");

            // Se añaden los datos básicos del usuario (nombre, apellido, email, contraseña, tipo)
            Element nombre = doc.createElement("nombre");
            nombre.appendChild(doc.createTextNode(usuario.getNombre()));
            usuarioElem.appendChild(nombre);

            Element apellido = doc.createElement("apellido");
            apellido.appendChild(doc.createTextNode(usuario.getApellido()));
            usuarioElem.appendChild(apellido);

            Element email = doc.createElement("email");
            email.appendChild(doc.createTextNode(usuario.getEmail()));
            usuarioElem.appendChild(email);

            Element passwd = doc.createElement("passwd");
            passwd.appendChild(doc.createTextNode(usuario.getPasswd()));
            usuarioElem.appendChild(passwd);

            Element tipo = doc.createElement("tipo");

            // Se asigna el tipo de usuario (en este caso siempre "Usuario")
            tipo.appendChild(doc.createTextNode(String.valueOf(Usuario.TipoUsuario.Usuario)));
            usuarioElem.appendChild(tipo);

            // Se crea el nodo que contendrá todos los puzzles del usuario
            Element puzzlesElem = doc.createElement("puzzles");

            // Se recorren los puzzles del usuario y se insertan uno a uno en el XML
            for (Puzzle p : usuario.getPuzzles()) {
                Element puzzleElem = doc.createElement("puzzle");

                Element autor = doc.createElement("autor");
                autor.appendChild(doc.createTextNode(p.getAutor()));
                puzzleElem.appendChild(autor);

                Element tiempo = doc.createElement("tiempo");
                tiempo.appendChild(doc.createTextNode(String.valueOf(p.getTiempo())));
                puzzleElem.appendChild(tiempo);

                Element piezas = doc.createElement("piezas");
                piezas.appendChild(doc.createTextNode(String.valueOf(p.getPiezas())));
                puzzleElem.appendChild(piezas);

                Element dificultad = doc.createElement("dificultad");
                dificultad.appendChild(doc.createTextNode(p.getDificultad().toString()));
                puzzleElem.appendChild(dificultad);

                Element descripcion = doc.createElement("descripcion");
                descripcion.appendChild(doc.createTextNode(p.getDescripcion()));
                puzzleElem.appendChild(descripcion);

                Element color = doc.createElement("color");
                color.appendChild(doc.createTextNode(String.valueOf(p.isColor())));
                puzzleElem.appendChild(color);

                Element valoracion = doc.createElement("valoracion");
                valoracion.appendChild(doc.createTextNode(String.valueOf(p.getValoracion())));
                puzzleElem.appendChild(valoracion);

                // Se añade el puzzle al nodo de puzzles del usuario
                puzzlesElem.appendChild(puzzleElem);
            }

            // Se añade el conjunto de puzzles al usuario
            usuarioElem.appendChild(puzzlesElem);

            // Finalmente, se añade el nuevo usuario al documento XML (dentro del nodo raíz)
            doc.getDocumentElement().appendChild(usuarioElem);

            // Se normaliza el documento (opcional pero recomendable)
            doc.normalizeDocument();

            // Se preparan los objetos necesarios para escribir el documento XML a disco
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Configuración de salida del XML
            transformer.setOutputProperty(OutputKeys.INDENT, "no"); // sin sangría
            transformer.setOutputProperty(OutputKeys.METHOD, "xml"); // formato XML
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // codificación UTF-8
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); // incluir cabecera XML

            // Se escribe el documento XML actualizado en el archivo "fichero"
            transformer.transform(new DOMSource(doc), new StreamResult(fichero));

            // Si todo va bien, se devuelve true indicando exito
            return true;

            // Manejo de excepciones específicas
        } catch (DuplicateEntry | DataFullException e) {
            throw e; // se relanzan las excepciones conocidas
        } catch (Exception e) {
            // Para cualquier otro error, se lanza una excepción personalizada
            throw new InsercionException("Error al insertar: " + e.getMessage());
        }
    }

    @Override
    public boolean eliminar(String email) throws DataEmptyAccess, DeleteException, DataAccessException, ObjectNotExist {

        // Control de errores de argumento
        if (email == null || email.isEmpty())
            throw new DataEmptyAccess("El email está vacío");

        try {
            // Si el fichero está vacío (no hay usuarios registrados), se lanza una excepción
            if (vacio()) {
                throw new ObjectNotExist("No hay usuarios para eliminar");
            }

            // Se crea una fábrica de constructores de documentos XML (DOM)
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // A partir de la fábrica, se obtiene un "builder" que permite leer o crear documentos XML
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Se carga (parsea) el documento XML desde el archivo existente
            Document doc = builder.parse(fichero);

            // Se obtiene una lista con todos los elementos <usuario> del XML
            NodeList usuarios = doc.getElementsByTagName("usuario");

            // Variable para saber si se ha eliminado algún usuario
            boolean eliminado = false;

            // Se recorre la lista de usuarios
            for (int i = 0; i < usuarios.getLength(); i++) {
                Element u = (Element) usuarios.item(i);

                // Se obtiene el texto del primer elemento <email> dentro del usuario actual
                String emailUsuario = u.getElementsByTagName("email").item(0).getTextContent();

                // Si el email coincide (ignorando mayúsculas/minúsculas), se elimina este nodo
                if (emailUsuario.equalsIgnoreCase(email)) {
                    // Se elimina el nodo <usuario> del nodo padre (<usuarios>)
                    u.getParentNode().removeChild(u);
                    eliminado = true; // Marcamos que se ha eliminado correctamente
                    break; // Se rompe el bucle, ya no hace falta seguir buscando
                }
            }

            // Si después de recorrer la lista no se ha encontrado el usuario, se lanza excepción
            if (!eliminado)
                throw new ObjectNotExist("Usuario no encontrado");

            // Se normaliza el documento (para limpiar nodos vacíos y mantener estructura coherente)
            doc.normalizeDocument();

            // Se preparan las herramientas para escribir los cambios en el fichero XML
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            // Se configuran las propiedades del XML de salida
            transformer.setOutputProperty(OutputKeys.INDENT, "no"); // sin sangría
            transformer.setOutputProperty(OutputKeys.METHOD, "xml"); // formato XML
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // codificación
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); // incluir cabecera

            // Se escribe el documento actualizado sobre el mismo fichero original
            transformer.transform(new DOMSource(doc), new StreamResult(fichero));

            // Si todo sale bien, se devuelve true indicando éxito
            return true;
            //Manejo de excepciones
        } catch (ObjectNotExist e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar: " + e.getMessage());
        }
    }

    @Override
    public boolean actualizar(Object obj) throws ObjectNotExist, DataEmptyAccess, InsercionException{

        // Si el objeto recibido es nulo, se lanza una excepción indicando que está vacío
        if (obj == null) {
            throw new DataEmptyAccess("El objeto usuario está vacío");
        }
        // Se hace un cast del objeto genérico a tipo Usuario
        Usuario usuarioActualizado = (Usuario) obj;

        try {
            // Si el archivo XML está vacío, no hay nada que actualizar → se lanza excepción
            if (vacio()) {
                throw new ObjectNotExist("Archivo vacío");
            }

            // Se prepara el parser XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Se carga (parsea) el documento XML desde el fichero existente
            Document doc = builder.parse(fichero);

            // Se obtienen todos los nodos <usuario> del XML
            NodeList usuarios = doc.getElementsByTagName("usuario");
            boolean encontrado = false; // Para saber si se localizó el usuario

            // Se recorre la lista de usuarios para buscar el que tenga el mismo email
            for (int i = 0; i < usuarios.getLength(); i++) {
                Element u = (Element) usuarios.item(i);

                // Se compara el email del usuario del XML con el del usuario actualizado
                if (u.getElementsByTagName("email").item(0).getTextContent()
                        .equalsIgnoreCase(usuarioActualizado.getEmail())) {
                    // Si coincide, se elimina el nodo completo del XML
                    u.getParentNode().removeChild(u);
                    encontrado = true; // Se marca que fue encontrado
                    break; // Se sale del bucle (solo se actualiza uno)
                }
            }

            // Si el usuario no se encontró en el XML, se lanza una excepción
            if (!encontrado)
                throw new ObjectNotExist("El usuario no existe en el archivo XML");

            // Guardar el documento actualizado (sin el usuario viejo)

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fichero);
            // Se sobrescribe el fichero XML eliminando la versión anterior del usuario
            transformer.transform(source, result);

            // Insertar el usuario actualizado
            // para agregar el nuevo objeto Usuario con los datos actualizados
            insertar(usuarioActualizado);

            // Si todo va bien, se devuelve true
            return true;

        } catch (ObjectNotExist | DataEmptyAccess e) {
            // Se relanzan las excepciones conocidas tal cual
            throw e;
        } catch (Exception e) {
            // Si ocurre cualquier otro error (parseo, escritura, etc.), se lanza excepción personalizada
            throw new InsercionException("Error al actualizar: " + e.getMessage());
        }
    }

    @Override
    public List<Usuario> buscar() throws DataEmptyAccess {

        // Se crea una lista vacía donde se guardarán los usuarios leídos del XML
        List<Usuario> listaUsuarios = new ArrayList<>();

        // Si el fichero XML está vacío, no hay datos que leer → se lanza excepción
        if (vacio())
            throw new DataEmptyAccess("No hay datos");

        try {

            // Se prepara el parser XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Se carga (parsea) el contenido del fichero XML en un objeto Document (DOM)
            Document doc = builder.parse(fichero);

            // Se obtienen todos los elementos <usuario> del documento
            NodeList usuarios = doc.getElementsByTagName("usuario");

            // Se recorre la lista de usuarios encontrados en el XML
            for (int i = 0; i < usuarios.getLength(); i++) {
                Element u = (Element) usuarios.item(i);

                // Se extraen los datos básicos del usuario desde las etiquetas del XML
                String nombre = u.getElementsByTagName("nombre").item(0).getTextContent();
                String apellido = u.getElementsByTagName("apellido").item(0).getTextContent();
                String email = u.getElementsByTagName("email").item(0).getTextContent();
                String passwd = u.getElementsByTagName("passwd").item(0).getTextContent();
                Usuario.TipoUsuario tipo = Usuario.TipoUsuario.valueOf(
                        u.getElementsByTagName("tipo").item(0).getTextContent()
                );

                // Se leen los puzzles asociados al usuario
                List<Puzzle> puzzles = new ArrayList<>();

                // Se accede al nodo <puzzles> y se obtienen todos los <puzzle> que contiene
                NodeList puzzlesNodes = ((Element) u.getElementsByTagName("puzzles").item(0))
                        .getElementsByTagName("puzzle");

                // Se recorre cada <puzzle> dentro del usuario
                for (int j = 0; j < puzzlesNodes.getLength(); j++) {
                    Element p = (Element) puzzlesNodes.item(j);

                    // Se crea un nuevo objeto Puzzle con los valores leídos del XML
                    Puzzle puzzle = new Puzzle(
                            p.getElementsByTagName("autor").item(0).getTextContent(),
                            Integer.parseInt(p.getElementsByTagName("tiempo").item(0).getTextContent()),
                            Integer.parseInt(p.getElementsByTagName("piezas").item(0).getTextContent()),
                            Puzzle.Dificultades.valueOf(p.getElementsByTagName("dificultad").item(0).getTextContent()),
                            p.getElementsByTagName("descripcion").item(0).getTextContent(),
                            Boolean.parseBoolean(p.getElementsByTagName("color").item(0).getTextContent()),
                            Integer.parseInt(p.getElementsByTagName("valoracion").item(0).getTextContent())
                    );

                    // Se añade el puzzle a la lista de puzzles del usuario actual
                    puzzles.add(puzzle);
                }

                // Se crea el objeto Usuario con toda la información leída
                Usuario usuario = new Usuario(nombre, apellido, email, passwd, tipo, puzzles);

                // Se añade el usuario completo a la lista de resultados
                listaUsuarios.add(usuario);
            }

        } catch (Exception e) {
            // Si ocurre algún error durante la lectura o parseo del XML,
            // se lanza una excepción indicando el problema
            throw new DataEmptyAccess("Error leyendo XML: " + e.getMessage());
        }

        // Se devuelve la lista de usuarios con todos sus datos y puzzles
        return listaUsuarios;
    }

    @Override
    public List<String> buscarAtributo(String atributo) throws DataEmptyAccess {
        // Se obtiene la lista completa de usuarios desde el método buscar() (que lee el XML)
        List<Usuario> listaUsuarios = buscar();

        // Lista donde se almacenarán los valores encontrados según el atributo solicitado
        List<String> encontrado = new ArrayList<>();

        // Si no hay usuarios en la lista, se lanza una excepción
        if (listaUsuarios.isEmpty())
            throw new DataEmptyAccess("No hay datos");

        // Se recorre cada usuario de la lista
        for (Usuario usuario : listaUsuarios) {

            // Por cada usuario, se recorren todos sus puzzles
            for (Puzzle p : usuario.getPuzzles()) {

                // Según el atributo solicitado, se obtiene el valor correspondiente del puzzle
                switch (atributo) {
                    case "autor":
                        encontrado.add(p.getAutor());
                        break;
                    case "tiempo":
                        encontrado.add(String.valueOf(p.getTiempo()));
                        break;
                    case "piezas":
                        encontrado.add(String.valueOf(p.getPiezas()));
                        break;
                    case "dificultad":
                        encontrado.add(p.getDificultad().toString());
                        break;
                    case "valoracion":
                        encontrado.add(String.valueOf(p.getValoracion()));
                        break;
                }
            }
        }

        // Se ordena alfabéticamente (o numéricamente si aplica) la lista de resultados encontrados
        Collections.sort(encontrado);

        // Finalmente, se devuelve la lista de valores encontrados
        return encontrado;
    }

    @Override
    public boolean bloquearUsuario(Object obj) throws DataFullException, InsercionException {
        // Se convierte el objeto genérico a un Usuario
        Usuario usuario = (Usuario) obj;

        try {
            // Intenta eliminar al usuario del fichero principal
            eliminar(usuario.getEmail());
        } catch (Exception ignored) {}

        // Inserta al usuario en el archivo de usuarios bloqueados
        return insertarBloqueado(usuario);
    }

    private boolean insertarBloqueado(Usuario usuario) throws InsercionException, DataFullException {
        try {
            // Si el fichero está lleno (según alguna regla de capacidad), lanza una excepción
            if (lleno()) {
                throw new DataFullException("No hay espacio en el fichero");
            }

            // Se prepara el parser XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            // Si el archivo de bloqueados no existe o está vacío, se crea un nuevo XML con el nodo raíz <usuarios>
            if (!ficheroBloq.exists() || ficheroBloq.length() == 0) {
                doc = builder.newDocument();
                Element root = doc.createElement("usuarios");
                doc.appendChild(root);
            } else {
                // Si ya existe, se carga el documento existente
                doc = builder.parse(ficheroBloq);
            }

            // Se crea el nuevo nodo <usuario> para agregar al XML de bloqueados
            Element usuarioElem = doc.createElement("usuario");

            // Nombre
            Element nombre = doc.createElement("nombre");
            nombre.appendChild(doc.createTextNode(usuario.getNombre()));
            usuarioElem.appendChild(nombre);

            // Apellido
            Element apellido = doc.createElement("apellido");
            apellido.appendChild(doc.createTextNode(usuario.getApellido()));
            usuarioElem.appendChild(apellido);

            // Email
            Element email = doc.createElement("email");
            email.appendChild(doc.createTextNode(usuario.getEmail()));
            usuarioElem.appendChild(email);

            // Contraseña
            Element passwd = doc.createElement("passwd");
            passwd.appendChild(doc.createTextNode(usuario.getPasswd()));
            usuarioElem.appendChild(passwd);

            // Tipo de usuario (por ejemplo, normal, admin, etc.)
            Element tipo = doc.createElement("tipo");
            tipo.appendChild(doc.createTextNode(usuario.getTipoUsuario().toString()));
            usuarioElem.appendChild(tipo);

            // Nodo vacío para "puzzles" (usuarios bloqueados no tienen puzzles activos)
            usuarioElem.appendChild(doc.createElement("puzzles"));

            // Se añade el nuevo <usuario> al documento
            doc.getDocumentElement().appendChild(usuarioElem);

            // Se normaliza el documento (buenas prácticas DOM)
            doc.normalizeDocument();

            // --- Se guarda el documento actualizado en el fichero de bloqueados ---
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            // Se sobrescribe el archivo XML con los cambios
            transformer.transform(new DOMSource(doc), new StreamResult(ficheroBloq));

            // Si todo sale bien, devuelve true
            return true;

        } catch (Exception e) {
            // Si ocurre algún error (DOM, IO, etc.), se lanza una excepción personalizada
            throw new InsercionException("Error al insertar bloqueado: " + e.getMessage());
        }
    }

    @Override
    public Puzzle[] getTopFive() throws DataEmptyAccess {

        // Se obtiene la lista de todos los usuarios del sistema leyendo desde el XML
        List<Usuario> listaUsuarios = buscar();

        // Lista temporal donde se almacenarán todos los puzzles de todos los usuarios
        List<Puzzle> puzzles = new ArrayList<>();

        // Se agregan todos los puzzles de cada usuario a la lista general
        for (Usuario u : listaUsuarios)
            puzzles.addAll(u.getPuzzles());

        // Se ordena la lista de puzzles en orden descendente según el tiempo (el puzzle con mayor tiempo aparece primero)
        puzzles.sort(Comparator.comparingDouble(Puzzle::getTiempo).reversed());

        // Se determina el número de elementos a devolver (máximo 5 o menos si hay menos puzzles)
        int top = Math.min(5, puzzles.size());

        // Se crea un array para almacenar los puzzles del top
        Puzzle[] arrayPuzzle = new Puzzle[top];

        // Se copian los primeros 'top' puzzles de la lista ordenada al array
        for (int i = 0; i < top; i++)
            arrayPuzzle[i] = puzzles.get(i);

        // Se devuelve el array con los mejores 5 puzzles (o menos si hay menos de 5)
        return arrayPuzzle;
    }

    @Override
    public String mejorTiempo() {

        // El método buscarAtributo("tiempo") recorre el XML y devuelve una lista de Strings con todos los tiempos.
        List<String> tiempos = buscarAtributo("tiempo");

        // Lista donde se guardarán los tiempos convertidos a enteros
        List<Integer> tiemposInt = new ArrayList<>();

        // Se intenta convertir cada valor de tiempo (String) a entero
        for (String tiempo : tiempos) {
            try {
                tiemposInt.add(Integer.parseInt(tiempo)); // Conversión exitosa
            } catch (NumberFormatException e) {
                // Si algún valor no es un número válido, se muestra un aviso por consola y se ignora
                System.err.println("Valor de tiempo no válido: " + tiempo);
            }
        }

        // Si después de convertir no hay valores válidos, se devuelve un mensaje informativo
        if (tiemposInt.isEmpty()) {
            return "No hay tiempos disponibles";
        }

        // Ordena la lista de tiempos en orden ascendente (de menor a mayor) el primer valor será el menor (el mejor tiempo)
        tiemposInt.sort(Comparator.naturalOrder());

        // Se obtiene el primer elemento (el tiempo más bajo)
        int mejor = tiemposInt.get(0);

        // Se devuelve el mejor tiempo como String
        return String.valueOf(mejor);
    }
}