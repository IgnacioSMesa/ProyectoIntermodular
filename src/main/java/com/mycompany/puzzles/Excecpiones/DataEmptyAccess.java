package com.mycompany.puzzles.Excecpiones;

public class DataEmptyAccess extends RuntimeException {
    public DataEmptyAccess(String message) {
        super(message);
    }
    public DataEmptyAccess(String message, Throwable cause) {
        super(message, cause);
    }
}