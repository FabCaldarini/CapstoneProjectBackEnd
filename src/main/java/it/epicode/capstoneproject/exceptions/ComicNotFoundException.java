package it.epicode.capstoneproject.exceptions;

public class ComicNotFoundException extends RuntimeException {
    public ComicNotFoundException(String message) {
        super(message);
    }
}

