package com.example.pokedex.infrastructure.pokeapi;

import com.example.pokedex.application.exception.ExternalServiceException;
import com.example.pokedex.application.exception.PokemonNotFoundException;
import com.example.pokedex.domain.model.PokemonInfo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PokeApiClientTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void extractsEnglishFlavorTextAndNormalizesWhitespace() {
        String body = """
                {
                  "name": "mewtwo",
                  "is_legendary": true,
                  "habitat": { "name": "rare" },
                  "flavor_text_entries": [
                    {
                      "flavor_text": "Ignored text",
                      "language": { "name": "fr" }
                    },
                    {
                      "flavor_text": "It was created\\nby a scientist\\fafter years.",
                      "language": { "name": "en" }
                    }
                  ]
                }
                """;
        server.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        String baseUrl = server.url("/api/v2").toString();
        PokeApiClient client = new PokeApiClient(new RestTemplate(), baseUrl);

        PokemonInfo info = client.fetchPokemonInfo("mewtwo");

        assertThat(info.name()).isEqualTo("mewtwo");
        assertThat(info.habitat()).isEqualTo("rare");
        assertThat(info.isLegendary()).isTrue();
        assertThat(info.description()).isEqualTo("It was created by a scientist after years.");
    }

    @Test
    void defaultsHabitatToUnknownWhenMissing() {
        String body = """
                {
                  "name": "articuno",
                  "is_legendary": true,
                  "habitat": null,
                  "flavor_text_entries": [
                    {
                      "flavor_text": "A legendary bird.",
                      "language": { "name": "en" }
                    }
                  ]
                }
                """;
        server.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        String baseUrl = server.url("/api/v2").toString();
        PokeApiClient client = new PokeApiClient(new RestTemplate(), baseUrl);

        PokemonInfo info = client.fetchPokemonInfo("articuno");

        assertThat(info.habitat()).isEqualTo("unknown");
        assertThat(info.isLegendary()).isTrue();
    }


    @Test
    void returnsEmptyDescriptionWhenFlavorTextEntriesMissing() {
        String body = """
                {
                  "name": "ditto",
                  "is_legendary": false,
                  "habitat": { "name": "urban" },
                  "flavor_text_entries": []
                }
                """;
        server.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        String baseUrl = server.url("/api/v2").toString();
        PokeApiClient client = new PokeApiClient(new RestTemplate(), baseUrl);

        PokemonInfo info = client.fetchPokemonInfo("ditto");

        assertThat(info.description()).isEmpty();
    }

    @Test
    void throwsPokemonNotFoundWhenApiReturns404() {
        server.enqueue(new MockResponse().setResponseCode(404));

        String baseUrl = server.url("/api/v2").toString();
        PokeApiClient client = new PokeApiClient(new RestTemplate(), baseUrl);

        assertThatThrownBy(() -> client.fetchPokemonInfo("missingno"))
                .isInstanceOf(PokemonNotFoundException.class);
    }

    @Test
    void throwsPokemonNotFoundWhenBodyIsNull() {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(""));

        String baseUrl = server.url("/api/v2").toString();
        PokeApiClient client = new PokeApiClient(new RestTemplate(), baseUrl);

        assertThatThrownBy(() -> client.fetchPokemonInfo("mew"))
                .isInstanceOf(PokemonNotFoundException.class);
    }

    @Test
    void throwsExternalServiceExceptionOnServerError() {
        server.enqueue(new MockResponse().setResponseCode(500));

        String baseUrl = server.url("/api/v2").toString();
        PokeApiClient client = new PokeApiClient(new RestTemplate(), baseUrl);

        assertThatThrownBy(() -> client.fetchPokemonInfo("mew"))
                .isInstanceOf(ExternalServiceException.class);
    }
}
