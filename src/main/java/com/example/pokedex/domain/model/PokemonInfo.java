package com.example.pokedex.domain.model;

public record PokemonInfo(
        String name,
        String description,
        String habitat,
        boolean isLegendary
) {
}
