package com.example.pokedex.application.exception;

public class PokemonNotFoundException extends RuntimeException {
    public PokemonNotFoundException(String pokemonName) {
        super("Pokemon not found: " + pokemonName);
    }
}
