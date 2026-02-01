package com.example.pokedex.api;

import com.example.pokedex.api.dto.PokemonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pokemon")
public class PokemonController {


    public PokemonController(){}

    @GetMapping("/{name}")
    public PokemonResponse getPokemon(@PathVariable String name) {
        return toResponse(name);
    }

    @GetMapping("/translated/{name}")
    public PokemonResponse getTranslatedPokemon(@PathVariable String name) {
        return toResponse(name);
    }

    private PokemonResponse toResponse(String name) {
        return new PokemonResponse(
                name,
                "a mock description",
                "a mock habitat",
                true
        );
    }
}
