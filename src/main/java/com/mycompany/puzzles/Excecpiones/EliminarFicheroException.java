package com.mycompany.puzzles.Excecpiones;

public class EliminarFicheroException extends Exception {
    public EliminarFicheroException(String message) {
        super(message);
    }
    public EliminarFicheroException(String message, Throwable cause) {
        super(message, cause);
    }
}
