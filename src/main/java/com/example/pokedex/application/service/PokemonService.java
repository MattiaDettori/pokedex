package com.example.pokedex.application.service;

import com.example.pokedex.application.exception.TranslationUnavailableException;
import com.example.pokedex.domain.model.PokemonInfo;
import com.example.pokedex.domain.ports.PokemonInfoProvider;
import com.example.pokedex.domain.ports.TranslationProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class PokemonService {

    private final PokemonInfoProvider pokemonInfoProvider;
    private final TranslationProvider translationProvider;

    public PokemonService(PokemonInfoProvider pokemonInfoProvider,
                          TranslationProvider translationProvider) {
        this.pokemonInfoProvider = pokemonInfoProvider;
        this.translationProvider = translationProvider;
    }

    @Cacheable(cacheNames = "pokemonInfo",
            key = "#pokemonName == null ? '' : #pokemonName.trim().toLowerCase()")
    public PokemonInfo getPokemonInfo(String pokemonName) {
        return pokemonInfoProvider.fetchPokemonInfo(normalize(pokemonName));
    }

    @Cacheable(cacheNames = "pokemonTranslated",
            key = "#pokemonName == null ? '' : #pokemonName.trim().toLowerCase()")
    public PokemonInfo getTranslatedPokemonInfo(String pokemonName) {
        PokemonInfo info = getPokemonInfo(pokemonName);
        String translated = translateDescription(info);
        return new PokemonInfo(
                info.name(),
                translated,
                info.habitat(),
                info.isLegendary()
        );
    }

    private String translateDescription(PokemonInfo info) {
        try {
            if (isYodaTranslation(info)) {
                return translationProvider.translateToYoda(info.description());
            }
            return translationProvider.translateToShakespeare(info.description());
        } catch (TranslationUnavailableException ex) {
            return info.description();
        }
    }

    private boolean isYodaTranslation(PokemonInfo info) {
        return info.isLegendary() || "cave".equalsIgnoreCase(info.habitat());
    }

    private String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
    }
}
