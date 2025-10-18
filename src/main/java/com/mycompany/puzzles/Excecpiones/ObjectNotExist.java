package com.mycompany.puzzles.Excecpiones;

public class ObjectNotExist extends RuntimeException {
    public ObjectNotExist(String message) {
        super(message);
    }
    public ObjectNotExist(String message, Throwable cause){
        super(message, cause);
    }
}
