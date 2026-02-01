package com.example.pokedex.api;

import com.example.pokedex.api.dto.PokemonResponse;
import com.example.pokedex.application.service.PokemonService;
import com.example.pokedex.domain.model.PokemonInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/{name}")
    public PokemonResponse getPokemon(@PathVariable String name) {
        return toResponse(pokemonService.getPokemonInfo(name));
    }

    @GetMapping("/translated/{name}")
    public PokemonResponse getTranslatedPokemon(@PathVariable String name) {
        return toResponse(pokemonService.getTranslatedPokemonInfo(name));
    }

    private PokemonResponse toResponse(PokemonInfo info) {
        return new PokemonResponse(
                info.name(),
                info.description(),
                info.habitat(),
                info.isLegendary()
        );
    }
}
