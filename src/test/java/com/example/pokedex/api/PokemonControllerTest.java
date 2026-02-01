package com.example.pokedex.api;

import com.example.pokedex.api.dto.PokemonResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PokemonControllerTest {
    private static MockWebServer pokeApiServer;
    private static MockWebServer translationsServer;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void startServers() throws IOException {
        pokeApiServer = new MockWebServer();
        translationsServer = new MockWebServer();
        pokeApiServer.start();
        translationsServer.start();
    }

    @AfterAll
    static void stopServers() throws IOException {
        if (pokeApiServer != null) {
            pokeApiServer.shutdown();
        }
        if (translationsServer != null) {
            translationsServer.shutdown();
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("pokedex.pokeapi.base-url",
                () -> pokeApiServer.url("/api/v2").toString());
        registry.add("pokedex.funtranslations.base-url",
                () -> translationsServer.url("/").toString());
    }

    @Test
    void returnsBasicPokemonInfo() {
        pokeApiServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "name": "mewtwo",
                          "is_legendary": true,
                          "habitat": { "name": "rare" },
                          "flavor_text_entries": [
                            {
                              "flavor_text": "It was created\\nby a scientist.",
                              "language": { "name": "en" }
                            }
                          ]
                        }
                        """));

        PokemonResponse response = restTemplate
                .getForObject("/pokemon/mewtwo", PokemonResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("mewtwo");
        assertThat(response.habitat()).isEqualTo("rare");
        assertThat(response.isLegendary()).isTrue();
        assertThat(response.description()).isEqualTo("It was created by a scientist.");
    }

    @Test
    void returnsTranslatedPokemonInfo() {
        pokeApiServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "name": "zubat",
                          "is_legendary": false,
                          "habitat": { "name": "cave" },
                          "flavor_text_entries": [
                            {
                              "flavor_text": "Lives in dark places.",
                              "language": { "name": "en" }
                            }
                          ]
                        }
                        """));
        translationsServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "contents": {
                            "translated": "In dark places, lives."
                          }
                        }
                        """));

        PokemonResponse response = restTemplate
                .getForObject("/pokemon/translated/zubat", PokemonResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("zubat");
        assertThat(response.habitat()).isEqualTo("cave");
        assertThat(response.isLegendary()).isFalse();
        assertThat(response.description()).isEqualTo("In dark places, lives.");
    }

    @Test
    void returnsNotFoundWhenPokemonDoesNotExist() {
        pokeApiServer.enqueue(new MockResponse().setResponseCode(404));

        ResponseEntity<String> response = restTemplate
                .getForEntity("/pokemon/does-not-exist", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void returnsInternalServerErrorWhenUpstreamFails() {
        pokeApiServer.enqueue(new MockResponse().setResponseCode(500));

        ResponseEntity<String> response = restTemplate
                .getForEntity("/pokemon/translated/mewtwo", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
