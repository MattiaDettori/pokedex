package com.example.pokedex.api;

import com.example.pokedex.api.dto.PokemonResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PokemonControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void returnBasicPokemonInfo() {
        String nameQueryParam = "mock";
        PokemonResponse pokemonResponse = restTemplate.getForObject("/pokemon/" + nameQueryParam, PokemonResponse.class);
        assertEquals(nameQueryParam, pokemonResponse.name());
        assertEquals("a mock description", pokemonResponse.description());
        assertEquals("a mock habitat", pokemonResponse.habitat());
        assertTrue(pokemonResponse.isLegendary());
    }
}
