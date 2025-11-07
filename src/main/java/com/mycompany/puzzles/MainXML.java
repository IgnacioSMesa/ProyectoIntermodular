package com.mycompany.puzzles;

import com.mycompany.puzzles.Clases.InterfazXML;
import com.mycompany.puzzles.Clases.Puzzle;
import com.mycompany.puzzles.Clases.Usuario;
import com.mycompany.puzzles.Excecpiones.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 🧩 Proyecto Puzzles (versión XML)
 * @author Ignacio & Natalia
 */
public class MainXML {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws DataFullException, DuplicateEntry, InsercionException {

        InterfazXML interfazXML = new InterfazXML();
        String email = "", passwd = "";

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║        🧩 BIENVENIDO A PUZZLES XML       ║");
        System.out.println("╚══════════════════════════════════════════╝");

        while (true) {
            System.out.println("\n╔════════════════ MENÚ PRINCIPAL ════════════════╗");
            System.out.println("║ 1️⃣  Registrarse                                ║");
            System.out.println("║ 2️⃣  Iniciar sesión                             ║");
            System.out.println("║ 3️⃣  Ver Ranking (Top 5)                        ║");
            System.out.println("║ 4️⃣  Bloquear usuario                           ║");
            System.out.println("║ 5️⃣  Mostrar mejor tiempo de todos los puzzles  ║");
            System.out.println("║ 6️⃣  Cerrar sesión                              ║");
            System.out.println("║ 7️⃣  Salir                                      ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.print("👉 Elige una opción: ");
            String opcion = sc.next();

            switch (opcion) {

                case "1": // REGISTRO
                    sc.nextLine();
                    System.out.println("\n🧍 REGISTRO DE NUEVO USUARIO");
                    System.out.print("👤 Nombre: ");
                    String nombre = sc.nextLine();
                    System.out.print("👥 Apellido: ");
                    String apellido = sc.nextLine();
                    System.out.print("📧 Email: ");
                    email = sc.next();
                    sc.nextLine();
                    System.out.print("🔒 Contraseña: ");
                    passwd = sc.nextLine();

                    List<Puzzle> puzzles = new ArrayList<>();
                    Usuario usuario = new Usuario(nombre, apellido, email, passwd, Usuario.TipoUsuario.Usuario, puzzles);

                    try {
                        if (interfazXML.insertar(usuario)) {
                            System.out.println("✅ Usuario registrado correctamente en usuarios.xml.");
                        } else {
                            System.out.println("⚠️ No se ha podido registrar el usuario.");
                        }
                    } catch (InsercionException e) {
                        System.out.println("❌ Error al insertar el usuario: " + e.getMessage());
                    }
                    esperarEnter();

                case "2": // LOGIN
                    if (email.equals("") || passwd.equals("")) {
                        System.out.println("\n🔐 INICIO DE SESIÓN");
                        System.out.print("📧 Email: ");
                        email = sc.next();
                        sc.nextLine();
                        System.out.print("🔑 Contraseña: ");
                        passwd = sc.nextLine();
                    }

                    boolean encontrado = false;
                    Usuario usuarioEncontrado = null;

                    for (Usuario u : interfazXML.buscar()) {
                        if (u.getEmail().equals(email) && u.getPasswd().equals(passwd)) {
                            encontrado = true;
                            usuarioEncontrado = u;
                            System.out.println("\n👋 ¡Hola, " + usuarioEncontrado.getNombre() + "!");
                            break;
                        }
                    }

                    if (!encontrado) {
                        System.out.println("❌ Usuario no encontrado o credenciales incorrectas.");
                        break;
                    }

                    System.out.print("¿Deseas insertar un nuevo puzzle? (s/n): ");
                    String respuesta = sc.next().trim().toLowerCase();

                    if (respuesta.equals("s")) {
                        sc.nextLine();

                        System.out.println("\n🧩 NUEVO PUZZLE");
                        System.out.print("✏️  Autor: ");
                        String autor = sc.nextLine();

                        System.out.print("⏱️  Tiempo de realización (en horas): ");
                        int tiempo = sc.nextInt();

                        System.out.print("🧱 Número de piezas: ");
                        int piezasPuzzle = sc.nextInt();
                        sc.nextLine();

                        System.out.print("🎚️  Dificultad (Facil, Medio, Dificil, Extremo): ");
                        String dificultadStr = sc.nextLine();

                        System.out.print("📝 Descripción breve: ");
                        String descripcion = sc.nextLine();

                        System.out.print("🎨 ¿Es a color? (true/false): ");
                        boolean color = sc.nextBoolean();

                        System.out.print("⭐ Valoración (1-5): ");
                        int valoracion = sc.nextInt();

                        Puzzle nuevoPuzzle = new Puzzle(autor, tiempo, piezasPuzzle,
                                Puzzle.Dificultades.valueOf(dificultadStr), descripcion, color, valoracion);
                        usuarioEncontrado.getPuzzles().add(nuevoPuzzle);

                        try {
                            interfazXML.actualizar(usuarioEncontrado);
                            System.out.println("✅ Puzzle añadido correctamente y guardado en usuarios.xml.");
                        } catch (Exception e) {
                            System.out.println("⚠️ Error al guardar el puzzle: " + e.getMessage());
                        }
                    } else {
                        System.out.println("🟡 No se insertó ningún puzzle.");
                    }
                    esperarEnter();
                    break;

                case "3": // RANKING
                    System.out.println("\n🏆 TOP 5 PUZZLES MEJOR VALORADOS:");
                    Puzzle[] pzl = interfazXML.getTopFive();
                    for (Puzzle p : pzl) {
                        System.out.println("   " + p);
                    }
                    esperarEnter();
                    break;

                case "4": // BLOQUEAR USUARIO
                    List<Usuario> users = interfazXML.buscar();
                    sc.nextLine();
                    System.out.print("🚫 Escriba el nombre del usuario que desea bloquear: ");
                    String nombreBloquear = sc.nextLine();

                    for (Usuario u : users) {
                        if (u.getNombre().equalsIgnoreCase(nombreBloquear)) {
                            try {
                                interfazXML.bloquearUsuario(u);
                                System.out.println("🔒 Usuario " + u.getNombre() + " bloqueado correctamente.");
                            } catch (DataFullException | InsercionException e) {
                                System.out.println("⚠️ Error al bloquear usuario: " + e.getMessage());
                            }
                        }
                    }
                    esperarEnter();
                    break;

                case "5":
                    System.out.println("\n⏳ El mejor tiempo registrado es: " + interfazXML.mejorTiempo());
                    esperarEnter();
                    break;

                case "6":
                    if (email.equals("") || passwd.equals("")) {
                        System.out.println("⚠️ No hay ninguna sesión activa para cerrar.");
                    } else {
                        email = "";
                        passwd = "";
                        System.out.println("👋 Sesión cerrada correctamente.");
                    }
                    esperarEnter();
                    break;

                case "7": // SALIR
                    System.out.println("\n👋 ¡Gracias por usar Puzzles XML! Hasta la próxima 🧩");
                    System.exit(0);

                default:
                    System.out.println("❌ Opción no válida. Intenta de nuevo.");
            }
        }
    }

    private static void esperarEnter() {
        System.out.println("\n🔹 Pulsa ENTER para continuar...");
        if (sc.hasNextLine()) sc.nextLine(); // limpia salto previo
        sc.nextLine(); // espera Enter
    }
}