package com.mycompany.puzzles.Excecpiones;

public class DeleteException extends Exception {
    public DeleteException(String message) {
        super(message);
    }
    public DeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
