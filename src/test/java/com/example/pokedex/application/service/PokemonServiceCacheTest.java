package com.example.pokedex.application.service;

import com.example.pokedex.PokedexApp;
import com.example.pokedex.domain.model.PokemonInfo;
import com.example.pokedex.domain.ports.PokemonInfoProvider;
import com.example.pokedex.domain.ports.TranslationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {PokedexApp.class, PokemonServiceCacheTest.Config.class})
public class PokemonServiceCacheTest {

    @Autowired
    private PokemonService service;

    @Autowired
    private CountingPokemonInfoProvider provider;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        if (cacheManager.getCache("pokemonInfo") != null) {
            cacheManager.getCache("pokemonInfo").clear();
        }
    }

    @Test
    void cachesPokemonInfoByNormalizedName() {
        PokemonInfo first = service.getPokemonInfo(" MewTwo ");
        PokemonInfo second = service.getPokemonInfo("mewtwo");

        assertThat(first).isEqualTo(second);
        assertThat(provider.calls()).isEqualTo(1);
    }

    @TestConfiguration
    static class Config {

        @Bean
        @Primary
        CountingPokemonInfoProvider pokemonInfoProvider() {
            return new CountingPokemonInfoProvider();
        }

        @Bean
        @Primary
        TranslationProvider translationProvider() {
            return new TranslationProvider() {
                @Override
                public String translateToYoda(String text) {
                    return text;
                }

                @Override
                public String translateToShakespeare(String text) {
                    return text;
                }
            };
        }

        @Bean
        @Primary
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("pokemonInfo", "pokemonTranslated");
        }
    }

    static class CountingPokemonInfoProvider implements PokemonInfoProvider {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public PokemonInfo fetchPokemonInfo(String pokemonName) {
            counter.incrementAndGet();
            return new PokemonInfo(pokemonName, "desc", "rare", false);
        }

        int calls() {
            return counter.get();
        }
    }
}
