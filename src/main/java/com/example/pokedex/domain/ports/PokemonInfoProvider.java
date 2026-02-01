package com.example.pokedex.domain.ports;

import com.example.pokedex.domain.model.PokemonInfo;

public interface PokemonInfoProvider {
    PokemonInfo fetchPokemonInfo(String pokemonName);
}
