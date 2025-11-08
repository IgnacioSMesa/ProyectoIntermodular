package com.mycompany.puzzles;

import com.mycompany.puzzles.Clases.*;
import com.mycompany.puzzles.Excecpiones.ArgumentException;
import com.mycompany.puzzles.Excecpiones.DataFullException;
import com.mycompany.puzzles.Excecpiones.DuplicateEntry;
import com.mycompany.puzzles.Excecpiones.InsercionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Ignacio y Natalia
 */
public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String args[]) throws ArgumentException {

        InterfazJSON interfazJSON = new InterfazJSON();
        InterfazXML interfazXML = new InterfazXML();
        OperacionesFicheros operacionesFicheros = new OperacionesFicheros();

        String email = "", passwd = "";

        int sistema = 0;

        while (true) {

            boolean salir = false;
            System.out.println("\nElige una opción" + "\n 1) Json" + "\n 2) XML" + "\n 3) Salir del programa");
            sistema = sc.nextInt();

            switch (sistema) {
                case 1:
                    System.out.println("╔══════════════════════════════════════════╗");
                    System.out.println("║       🧩 BIENVENIDO A PUZZLES JSON       ║");
                    System.out.println("╚══════════════════════════════════════════╝");

                    do {

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
                                List<Usuario> usuarios = new ArrayList<>();

                                Usuario usuario = new Usuario(nombre, apellido, email, passwd, Usuario.TipoUsuario.Usuario, puzzles);
                                usuarios.add(usuario);

                                try {
                                    if (interfazJSON.insertar(usuario)) {
                                        System.out.println("✅ Usuario registrado correctamente en usuarios.json.");
                                    } else {
                                        System.out.println("⚠️ No se ha podido registrar el usuario.");
                                    }
                                } catch (InsercionException | DataFullException | DuplicateEntry e) {
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

                                for (Usuario u : interfazJSON.buscar()) {
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
                                    sc.nextLine();

                                    System.out.print("🧱 Número de piezas: ");
                                    int piezasPuzzle = sc.nextInt();
                                    sc.nextLine();

                                    System.out.print("🎚️  Dificultad (Facil, Medio, Dificil, Extremo): ");
                                    String dificultadStr = sc.nextLine();

                                    System.out.print("📝 Descripción breve: ");
                                    String descripcion = sc.nextLine();

                                    System.out.print("🎨 ¿Es a color? (true/false): ");
                                    boolean color = sc.nextBoolean();
                                    sc.nextLine();

                                    System.out.print("⭐ Valoración (1-5): ");
                                    int valoracion = sc.nextInt();
                                    sc.nextLine();

                                    Puzzle nuevoPuzzle = new Puzzle(autor, tiempo, piezasPuzzle, Puzzle.Dificultades.valueOf(dificultadStr), descripcion, color, valoracion);
                                    usuarioEncontrado.getPuzzles().add(nuevoPuzzle);

                                    try {
                                        interfazJSON.actualizar(usuarioEncontrado);
                                        System.out.println("✅ Puzzle añadido correctamente y guardado en el fichero.");
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
                                Puzzle[] pzl = interfazJSON.getTopFive();
                                for (Puzzle p : pzl) {
                                    System.out.println("   " + p);
                                }
                                esperarEnter();
                                break;

                            case "4": // BLOQUEAR USUARIO
                                List<Usuario> users = interfazJSON.buscar();
                                sc.nextLine();
                                System.out.print("🚫 Escriba el nombre del usuario que desea bloquear: ");
                                String nombreBloquear = sc.nextLine();

                                for (Usuario u : users) {
                                    if (u.getNombre().equalsIgnoreCase(nombreBloquear)) {
                                        try {
                                            interfazJSON.bloquearUsuario(u);
                                            System.out.println("🔒 Usuario " + u.getNombre() + " bloqueado correctamente.");
                                        } catch (DataFullException | InsercionException e) {
                                            System.out.println("⚠️ Error al bloquear usuario: " + e.getMessage());
                                        }
                                    }
                                }
                                esperarEnter();
                                break;

                            case "5": // MEJOR TIEMPO
                                System.out.println("\n⏳ El mejor tiempo registrado es: " + interfazJSON.mejorTiempo());
                                esperarEnter();
                                break;

                            case "6": // CERRAR SESIÓN
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
                                salir = true;
                                break;
                            default:
                                System.out.println("❌ Opción no válida. Intenta de nuevo.");
                                break;
                        }
                    }while (!salir);
                    break;

                case 2:
                    System.out.println("╔══════════════════════════════════════════╗");
                    System.out.println("║       🧩 BIENVENIDO A PUZZLES XML        ║");
                    System.out.println("╚══════════════════════════════════════════╝");

                    do {

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
                                List<Usuario> usuarios = new ArrayList<>();

                                Usuario usuario = new Usuario(nombre, apellido, email, passwd, Usuario.TipoUsuario.Usuario, puzzles);
                                usuarios.add(usuario);

                                try {
                                    if (interfazXML.insertar(usuario)) {
                                        System.out.println("✅ Usuario registrado correctamente en usuarios.json.");
                                    } else {
                                        System.out.println("⚠️ No se ha podido registrar el usuario.");
                                    }
                                } catch (InsercionException | DataFullException | DuplicateEntry e) {
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
                                    sc.nextLine();

                                    System.out.print("🧱 Número de piezas: ");
                                    int piezasPuzzle = sc.nextInt();
                                    sc.nextLine();

                                    System.out.print("🎚️  Dificultad (Facil, Medio, Dificil, Extremo): ");
                                    String dificultadStr = sc.nextLine();

                                    System.out.print("📝 Descripción breve: ");
                                    String descripcion = sc.nextLine();

                                    System.out.print("🎨 ¿Es a color? (true/false): ");
                                    boolean color = sc.nextBoolean();
                                    sc.nextLine();

                                    System.out.print("⭐ Valoración (1-5): ");
                                    int valoracion = sc.nextInt();
                                    sc.nextLine();

                                    Puzzle nuevoPuzzle = new Puzzle(autor, tiempo, piezasPuzzle, Puzzle.Dificultades.valueOf(dificultadStr), descripcion, color, valoracion);
                                    usuarioEncontrado.getPuzzles().add(nuevoPuzzle);

                                    try {
                                        interfazXML.actualizar(usuarioEncontrado);
                                        System.out.println("✅ Puzzle añadido correctamente y guardado en el fichero.");
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

                            case "5": // MEJOR TIEMPO
                                System.out.println("\n⏳ El mejor tiempo registrado es: " + interfazXML.mejorTiempo());
                                esperarEnter();
                                break;

                            case "6": // CERRAR SESIÓN
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
                                salir = true;
                                break;
                            default:
                                System.out.println("❌ Opción no válida. Intenta de nuevo.");
                                break;
                        }
                    }while (!salir);
                    break;
                case 3:
                    try{
                        operacionesFicheros.eliminarFicheros();
                        operacionesFicheros.copiarFicheros();
                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }

                    System.out.println("\n👋 ¡Gracias por usar Puzzles! Hasta la próxima 🧩");
                    System.exit(0);
                    break;
            }
        }
    }
    private static void esperarEnter() {
        System.out.println("\n🔹 Pulsa ENTER para continuar...");
        if (sc.hasNextLine()) sc.nextLine();
        sc.nextLine();

    }
}