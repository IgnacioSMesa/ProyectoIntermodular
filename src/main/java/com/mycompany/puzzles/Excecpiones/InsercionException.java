package com.mycompany.puzzles.Excecpiones;

public class InsercionException extends Exception{
    public InsercionException(String message) {
        super(message);
    }
    public InsercionException(String message, Throwable cause) {
        super(message, cause);
    }
}
