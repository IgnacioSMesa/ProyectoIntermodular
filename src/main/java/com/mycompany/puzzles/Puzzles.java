/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.puzzles;

import com.mycompany.puzzles.Clases.InterfazJSON;
import com.mycompany.puzzles.Clases.Usuario;
import com.mycompany.puzzles.Excecpiones.DataFullException;
import com.mycompany.puzzles.Excecpiones.DuplicateEntry;
import com.mycompany.puzzles.Excecpiones.InsercionException;

import java.util.Scanner;

/**
 *
 * @author Ignacio y Natalia
 */
public class Puzzles {

    static Scanner sc=new Scanner(System.in);

    public static void main(String[] args) throws DataFullException, DuplicateEntry, InsercionException {

        InterfazJSON interfazJSON = new InterfazJSON();

        System.out.println("1) Registrarse");
        System.out.println("2) Iniciar sesión");
        System.out.println("3) Contraseña olvidada");
        System.out.println("4) Salir");

        String opcion = sc.next();

        switch (opcion) {
            case "1":
                System.out.println("Ingresa el nombre del usuario");
                String nombre = sc.nextLine();
                System.out.println("Ingresa el apellido");
                String apellido = sc.nextLine();
                System.out.println("Ingresa el email");
                String email = sc.next();
                System.out.println("Ingresa la contraseña");
                String passwd = sc.nextLine();

                Usuario usuario = new Usuario(nombre, apellido, email, passwd, Usuario.TipoUsuario.USUARIO);

                interfazJSON.insertar(usuario);

            case "2":

            case "3":
            case "4":
            default:
        }

    }

}