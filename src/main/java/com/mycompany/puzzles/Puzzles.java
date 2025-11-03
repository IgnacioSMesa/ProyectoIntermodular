/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.puzzles;

import com.mycompany.puzzles.Clases.InterfazJSON;
import com.mycompany.puzzles.Clases.Puzzle;
import com.mycompany.puzzles.Clases.Usuario;
import com.mycompany.puzzles.Excecpiones.DataFullException;
import com.mycompany.puzzles.Excecpiones.DuplicateEntry;
import com.mycompany.puzzles.Excecpiones.InsercionException;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Ignacio y Natalia
 */
public class Puzzles {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws DataFullException, DuplicateEntry, InsercionException {

        InterfazJSON interfazJSON = new InterfazJSON();

        System.out.println("1) Registrarse");
        System.out.println("2) Iniciar sesión");
        System.out.println("3) Contraseña olvidada");
        System.out.println("4) Salir");

        String email = "", passwd = "", opcion = sc.next();

        switch (opcion) {
            case "1":

                sc.nextLine();
                System.out.println("Ingresa el nombre del usuario");
                String nombre = sc.nextLine();
                System.out.println("Ingresa el apellido");
                String apellido = sc.nextLine();
                System.out.println("Ingresa el email");
                email = sc.next();
                sc.nextLine();
                System.out.println("Ingresa la contraseña");
                passwd = sc.nextLine();

                List<Puzzle> puzzles = new ArrayList<>();
                List<Usuario> usuarios = new ArrayList<>();

                Usuario usuario = new Usuario(nombre, apellido, email, passwd, Usuario.TipoUsuario.Usuario, puzzles);
                usuarios.add(usuario);

                try{
                    if (interfazJSON.insertar(usuario)) {
                        System.out.println("Se ha escrito el objeto Usuario en el archivo usuarios.json.");
                    } else {
                        System.out.println("No se ha escrito el objeto Usuario en el archivo usuarios.json.");
                    }
                }catch(InsercionException e){
                    System.out.println("No se pudo insertar el usuario: " + e.getMessage());
                }

            case "2":

                if (email.equals("") || passwd.equals("")) {
                    System.out.println("Ingresa el email");
                    email = sc.next();
                    sc.nextLine();
                    System.out.println("Ingresa la contraseña");
                    passwd = sc.nextLine();
                }

                boolean encontrado = false;
                Usuario usuarioEncontrado = null;

                for (Usuario u : interfazJSON.buscar()) {
                    if (u.getEmail().equals(email) && u.getPasswd().equals(passwd)) {
                        encontrado = true;
                        usuarioEncontrado = u;
                        System.out.println("holi" + usuarioEncontrado);
                        System.out.println("Usuario encontrado:");
                        System.out.println(u);
                        break;
                    }
                }

                if (!encontrado) {
                    System.out.println("Usuario no encontrado o credenciales incorrectas.");
                    break;
                }

                System.out.println("¿Deseas insertar un nuevo puzzle? (s/n)");
                String respuesta = sc.next().trim().toLowerCase();

                if (respuesta.equals("s")) {
                    sc.nextLine();

                    System.out.println("Introduce el autor del puzzle:");
                    String autor = sc.nextLine();

                    System.out.println("Introduce la media del puzzle (ejemplo: 4,5):");
                    float media = sc.nextFloat();

                    System.out.println("Introduce el número de piezas:");
                    int piezas = sc.nextInt();
                    sc.nextLine();

                    System.out.println("Introduce la dificultad (Facil, Media, Dificil, Extremo):");
                    String dificultadStr = sc.nextLine();

                    System.out.println("Introduce una breve descripción:");
                    String descripcion = sc.nextLine();

                    System.out.println("¿El puzzle es a color? (true/false):");
                    boolean color = sc.nextBoolean();

                    System.out.println("Introduce la valoración (entero 1-5):");
                    int valoracion = sc.nextInt();

                    // Crear el nuevo Puzzle
                    Puzzle nuevoPuzzle = new Puzzle(autor, media, piezas, Puzzle.Dificultades.valueOf(dificultadStr), descripcion, color, valoracion);

                    // Añadir a la lista del usuario
                    usuarioEncontrado.getPuzzles().add(nuevoPuzzle);

                    // Guardar nuevamente el usuario (reinsertar)
                    try {
                        System.out.println("\t" + usuarioEncontrado);
                        interfazJSON.actualizar(usuarioEncontrado);
                        System.out.println("🧩 Puzzle insertado correctamente y guardado en el fichero.");
                    } catch (Exception e) {
                        System.out.println("⚠️ No se pudo insertar el puzzle: " + e.getMessage());
                    }

                } else {
                    System.out.println("No se insertó ningún puzzle.");
                }

                break;

            case "3":
                break;
            case "4":
               break;
            default:
                System.out.println("Opción no válida.");
        }

    }

}