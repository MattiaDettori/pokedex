package com.example.pokedex.api.dto;

public record PokemonResponse(
        String name,
        String description,
        String habitat,
        boolean isLegendary
) {
}
