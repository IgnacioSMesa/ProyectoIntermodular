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

    private File fichero = new File("src/main/resources/Ficheros/usuarios.xml");
    private File ficheroBloq = new File("src/main/resources/Ficheros/usuariosBloqueados.xml");

    @Override
    public boolean lleno() {
        long TAM_MAX = 5L * 1024 * 1024 * 1024; // 5 GB
        return fichero.length() >= TAM_MAX;
    }

    @Override
    public boolean vacio() {
        return !fichero.exists() || fichero.length() == 0;
    }

    @Override
    public boolean insertar(Object obj) throws InsercionException, DataFullException, DuplicateEntry {
        Usuario usuario = (Usuario) obj;

        try {
            if (lleno()) throw new DataFullException("No hay espacio en el fichero");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            if (vacio()) {
                doc = builder.newDocument();
                Element root = doc.createElement("usuarios");
                doc.appendChild(root);
            } else {
                doc = builder.parse(fichero);
            }

            NodeList usuarios = doc.getElementsByTagName("usuario");
            for (int i = 0; i < usuarios.getLength(); i++) {
                Element u = (Element) usuarios.item(i);
                if (u.getElementsByTagName("email").item(0).getTextContent().equalsIgnoreCase(usuario.getEmail())) {
                    throw new DuplicateEntry("El usuario ya existe");
                }
            }

            Element usuarioElem = doc.createElement("usuario");

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
            tipo.appendChild(doc.createTextNode(String.valueOf(Usuario.TipoUsuario.Usuario)));
            usuarioElem.appendChild(tipo);

            Element puzzlesElem = doc.createElement("puzzles");
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

                puzzlesElem.appendChild(puzzleElem);
            }

            usuarioElem.appendChild(puzzlesElem);
            doc.getDocumentElement().appendChild(usuarioElem);

            doc.normalizeDocument();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            transformer.transform(new DOMSource(doc), new StreamResult(fichero));

            return true;

        } catch (DuplicateEntry | DataFullException e) {
            throw e;
        } catch (Exception e) {
            throw new InsercionException("Error al insertar: " + e.getMessage());
        }
    }

    @Override
    public boolean eliminar(String email) throws DataEmptyAccess, DeleteException, DataAccessException, ObjectNotExist {
        if (email == null || email.isEmpty())
            throw new DataEmptyAccess("El email está vacío");

        try {
            if (vacio()) throw new ObjectNotExist("No hay usuarios para eliminar");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fichero);

            NodeList usuarios = doc.getElementsByTagName("usuario");
            boolean eliminado = false;

            for (int i = 0; i < usuarios.getLength(); i++) {
                Element u = (Element) usuarios.item(i);
                if (u.getElementsByTagName("email").item(0).getTextContent().equalsIgnoreCase(email)) {
                    u.getParentNode().removeChild(u);
                    eliminado = true;
                    break;
                }
            }

            if (!eliminado)
                throw new ObjectNotExist("Usuario no encontrado");

            doc.normalizeDocument();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.transform(new DOMSource(doc), new StreamResult(fichero));

            return true;

        } catch (ObjectNotExist e) {
            throw e;
        } catch (Exception e) {
            throw new DeleteException("Error al eliminar: " + e.getMessage());
        }
    }

    @Override
    public boolean actualizar(Object obj) throws ObjectNotExist, DataEmptyAccess {
        if (obj == null) throw new DataEmptyAccess("El objeto usuario está vacío");

        Usuario usuarioActualizado = (Usuario) obj;
        try {
            if (vacio()) throw new ObjectNotExist("Archivo vacío");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fichero);

            NodeList usuarios = doc.getElementsByTagName("usuario");
            boolean encontrado = false;

            for (int i = 0; i < usuarios.getLength(); i++) {
                Element u = (Element) usuarios.item(i);
                if (u.getElementsByTagName("email").item(0).getTextContent()
                        .equalsIgnoreCase(usuarioActualizado.getEmail())) {
                    u.getParentNode().removeChild(u);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) throw new ObjectNotExist("El usuario no existe en el archivo XML");

            // Guardar el DOM sin el usuario viejo
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fichero);
            transformer.transform(source, result);

            // Ahora sí, insertar el nuevo usuario (el XML ya no lo contiene)
            insertar(usuarioActualizado);
            return true;

        } catch (ObjectNotExist | DataEmptyAccess e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar: " + e.getMessage());
        }
    }

    @Override
    public List<Usuario> buscar() throws DataEmptyAccess {
        List<Usuario> listaUsuarios = new ArrayList<>();
        if (vacio()) throw new DataEmptyAccess("No hay datos");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fichero);
            NodeList usuarios = doc.getElementsByTagName("usuario");

            for (int i = 0; i < usuarios.getLength(); i++) {
                Element u = (Element) usuarios.item(i);

                String nombre = u.getElementsByTagName("nombre").item(0).getTextContent();
                String apellido = u.getElementsByTagName("apellido").item(0).getTextContent();
                String email = u.getElementsByTagName("email").item(0).getTextContent();
                String passwd = u.getElementsByTagName("passwd").item(0).getTextContent();
                Usuario.TipoUsuario tipo = Usuario.TipoUsuario.valueOf(u.getElementsByTagName("tipo").item(0).getTextContent());

                List<Puzzle> puzzles = new ArrayList<>();
                NodeList puzzlesNodes = ((Element) u.getElementsByTagName("puzzles").item(0)).getElementsByTagName("puzzle");

                for (int j = 0; j < puzzlesNodes.getLength(); j++) {
                    Element p = (Element) puzzlesNodes.item(j);
                    Puzzle puzzle = new Puzzle(
                            p.getElementsByTagName("autor").item(0).getTextContent(),
                            Integer.parseInt(p.getElementsByTagName("tiempo").item(0).getTextContent()),
                            Integer.parseInt(p.getElementsByTagName("piezas").item(0).getTextContent()),
                            Puzzle.Dificultades.valueOf(p.getElementsByTagName("dificultad").item(0).getTextContent()),
                            p.getElementsByTagName("descripcion").item(0).getTextContent(),
                            Boolean.parseBoolean(p.getElementsByTagName("color").item(0).getTextContent()),
                            Integer.parseInt(p.getElementsByTagName("valoracion").item(0).getTextContent())
                    );
                    puzzles.add(puzzle);
                }

                listaUsuarios.add(new Usuario(nombre, apellido, email, passwd, tipo, puzzles));
            }

        } catch (Exception e) {
            throw new DataEmptyAccess("Error leyendo XML: " + e.getMessage());
        }

        return listaUsuarios;
    }

    @Override
    public List<String> buscarAtributo(String atributo) throws DataEmptyAccess {
        List<Usuario> listaUsuarios = buscar();
        List<String> encontrado = new ArrayList<>();

        if (listaUsuarios.isEmpty()) throw new DataEmptyAccess("No hay datos");

        for (Usuario usuario : listaUsuarios) {
            for (Puzzle p : usuario.getPuzzles()) {
                switch (atributo) {
                    case "autor": encontrado.add(p.getAutor()); break;
                    case "tiempo": encontrado.add(String.valueOf(p.getTiempo())); break;
                    case "piezas": encontrado.add(String.valueOf(p.getPiezas())); break;
                    case "dificultad": encontrado.add(p.getDificultad().toString()); break;
                    case "valoracion": encontrado.add(String.valueOf(p.getValoracion())); break;
                }
            }
        }

        Collections.sort(encontrado);
        return encontrado;
    }

    @Override
    public boolean bloquearUsuario(Object obj) throws DataFullException, InsercionException {
        Usuario usuario = (Usuario) obj;

        try {
            eliminar(usuario.getEmail());
        } catch (Exception ignored) {}

        return insertarBloqueado(usuario);
    }

    private boolean insertarBloqueado(Usuario usuario) throws InsercionException, DataFullException {
        try {
            if (lleno()) throw new DataFullException("No hay espacio en el fichero");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            if (!ficheroBloq.exists() || ficheroBloq.length() == 0) {
                doc = builder.newDocument();
                Element root = doc.createElement("usuarios");
                doc.appendChild(root);
            } else {
                doc = builder.parse(ficheroBloq);
            }

            Element usuarioElem = doc.createElement("usuario");
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
            tipo.appendChild(doc.createTextNode(usuario.getTipoUsuario().toString()));
            usuarioElem.appendChild(tipo);
            usuarioElem.appendChild(doc.createElement("puzzles"));

            doc.getDocumentElement().appendChild(usuarioElem);
            doc.normalizeDocument();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.transform(new DOMSource(doc), new StreamResult(ficheroBloq));

            return true;

        } catch (Exception e) {
            throw new InsercionException("Error al insertar bloqueado: " + e.getMessage());
        }
    }

    @Override
    public Puzzle[] getTopFive() throws DataEmptyAccess {
        List<Usuario> listaUsuarios = buscar();
        List<Puzzle> puzzles = new ArrayList<>();

        for (Usuario u : listaUsuarios) puzzles.addAll(u.getPuzzles());

        puzzles.sort(Comparator.comparingDouble(Puzzle::getTiempo).reversed());
        int top = Math.min(5, puzzles.size());
        Puzzle[] arrayPuzzle = new Puzzle[top];
        for (int i = 0; i < top; i++) arrayPuzzle[i] = puzzles.get(i);
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