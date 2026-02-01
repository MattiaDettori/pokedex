package com.example.pokedex.application.exception;

public class TranslationUnavailableException extends RuntimeException {
    public TranslationUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
