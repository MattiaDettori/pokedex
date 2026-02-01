package com.example.pokedex.application.service;

import com.example.pokedex.application.exception.TranslationUnavailableException;
import com.example.pokedex.domain.model.PokemonInfo;
import com.example.pokedex.domain.ports.PokemonInfoProvider;
import com.example.pokedex.domain.ports.TranslationProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PokemonServiceTest {

    @Test
    void usesYodaTranslationForLegendaryPokemon() {
        PokemonInfoProvider infoProvider = name -> new PokemonInfo(name, "original", "rare", true);
        TranslationProvider translations = new FixedTranslationProvider("yoda", "shakespeare");
        PokemonService service = new PokemonService(infoProvider, translations);

        PokemonInfo info = service.getTranslatedPokemonInfo("mewtwo");

        assertThat(info.description()).isEqualTo("yoda");
    }

    @Test
    void usesYodaTranslationForCavePokemon() {
        PokemonInfoProvider infoProvider = name -> new PokemonInfo(name, "original", "cave", false);
        TranslationProvider translations = new FixedTranslationProvider("yoda", "shakespeare");
        PokemonService service = new PokemonService(infoProvider, translations);

        PokemonInfo info = service.getTranslatedPokemonInfo("zubat");

        assertThat(info.description()).isEqualTo("yoda");
    }

    @Test
    void usesShakespeareTranslationForOtherPokemon() {
        PokemonInfoProvider infoProvider = name -> new PokemonInfo(name, "original", "forest", false);
        TranslationProvider translations = new FixedTranslationProvider("yoda", "shakespeare");
        PokemonService service = new PokemonService(infoProvider, translations);

        PokemonInfo info = service.getTranslatedPokemonInfo("pikachu");

        assertThat(info.description()).isEqualTo("shakespeare");
    }

    @Test
    void fallsBackToStandardDescriptionWhenTranslationFails() {
        PokemonInfoProvider infoProvider = name -> new PokemonInfo(name, "original", "forest", false);
        TranslationProvider translations = new FailingTranslationProvider();
        PokemonService service = new PokemonService(infoProvider, translations);

        PokemonInfo info = service.getTranslatedPokemonInfo("pikachu");

        assertThat(info.description()).isEqualTo("original");
    }

    private static class FixedTranslationProvider implements TranslationProvider {
        private final String yoda;
        private final String shakespeare;

        private FixedTranslationProvider(String yoda, String shakespeare) {
            this.yoda = yoda;
            this.shakespeare = shakespeare;
        }

        @Override
        public String translateToYoda(String text) {
            return yoda;
        }

        @Override
        public String translateToShakespeare(String text) {
            return shakespeare;
        }
    }

    private static class FailingTranslationProvider implements TranslationProvider {
        @Override
        public String translateToYoda(String text) {
            throw new TranslationUnavailableException("fail", null);
        }

        @Override
        public String translateToShakespeare(String text) {
            throw new TranslationUnavailableException("fail", null);
        }
    }
}
