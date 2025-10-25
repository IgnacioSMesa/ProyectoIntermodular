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

                Usuario usuario = new Usuario(nombre, apellido, email, passwd, Usuario.TipoUsuario.Usuario, puzzles);

                if (interfazJSON.insertar(usuario)) {
                    System.out.println("Se ha escrito el objeto Usuario en el archivo usuarios.json.");
                } else {
                    System.out.println("No se ha escrito el objeto Usuario en el archivo usuarios.json.");
                }

            case "2":

                if (email.equals("") || passwd.equals("")) {
                    System.out.println("Ingresa el email");
                    email = sc.next();
                    sc.nextLine();
                    System.out.println("Ingresa la contraseña");
                    passwd = sc.nextLine();
                }
                for(Usuario u : interfazJSON.buscar()){
                    if (u.getEmail().equals(email) && u.getPasswd().equals(passwd)) {
                        System.out.println("Usuario encontrado");
                        System.out.println(u);
                    }
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