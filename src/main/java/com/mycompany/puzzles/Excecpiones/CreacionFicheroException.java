package com.mycompany.puzzles.Excecpiones;

public class CreacionFicheroException extends Exception {
    public CreacionFicheroException(String message) {
        super(message);
    }
    public CreacionFicheroException(String message, Throwable cause) {
        super(message, cause);
    }
}
