package com.mycompany.puzzles.Excecpiones;

public class DuplicateEntry extends Exception {
    public DuplicateEntry(String message) {
        super(message);
    }
    public DuplicateEntry(String message, Throwable cause) {
        super(message, cause);
    }
}
