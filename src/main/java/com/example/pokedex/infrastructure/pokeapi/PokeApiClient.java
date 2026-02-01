package com.example.pokedex.infrastructure.pokeapi;

import com.example.pokedex.domain.model.PokemonInfo;
import com.example.pokedex.domain.ports.PokemonInfoProvider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Component
public class PokeApiClient implements PokemonInfoProvider {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PokeApiClient(RestTemplate restTemplate,
                         @Value("${pokedex.pokeapi.base-url:https://pokeapi.co/api/v2}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public PokemonInfo fetchPokemonInfo(String pokemonName) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("pokemon-species", pokemonName)
                .toUriString();
        try {
            ResponseEntity<PokemonSpeciesContract> response =
                    restTemplate.getForEntity(url, PokemonSpeciesContract.class);
            PokemonSpeciesContract body = response.getBody();
            if (body == null) {
                throw new IllegalArgumentException("Pokemon not found:" +pokemonName);
            }
            String description = extractEnglishFlavorText(body.flavorTextEntries());
            return new PokemonInfo(
                    body.name(),
                    description,
                    body.habitat() == null ? "unknown" : body.habitat().name(),
                    body.isLegendary()
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Pokemon not found:" +pokemonName);
        }
    }

    private String extractEnglishFlavorText(List<FlavorTextEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return "";
        }
        return entries.stream()
                .filter(entry -> entry.language() != null)
                .filter(entry -> "en".equalsIgnoreCase(entry.language().name()))
                .map(FlavorTextEntry::flavorText)
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::sanitizeFlavorText)
                .orElse("");
    }

    private String sanitizeFlavorText(String text) {
        return text.replace("\n", " ")
                .replace("\f", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonSpeciesContract(
            String name,
            Habitat habitat,
            @JsonProperty("is_legendary") boolean isLegendary,
            @JsonProperty("flavor_text_entries") List<FlavorTextEntry> flavorTextEntries
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Habitat(String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FlavorTextEntry(
            @JsonProperty("flavor_text") String flavorText,
            Language language
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Language(String name) {
    }
}
