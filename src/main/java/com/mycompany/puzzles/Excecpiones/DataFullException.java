package com.mycompany.puzzles.Excecpiones;

public class DataFullException extends Exception {
    public DataFullException(String message) {
        super(message);
    }
    public DataFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
