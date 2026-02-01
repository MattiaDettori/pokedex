package com.example.pokedex.infrastructure.translation;

import com.example.pokedex.application.exception.ExternalServiceException;
import com.example.pokedex.application.exception.TranslationUnavailableException;
import com.example.pokedex.domain.ports.TranslationProvider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class FunTranslationsClient implements TranslationProvider {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public FunTranslationsClient(RestTemplate restTemplate,
                                 @Value("${pokedex.funtranslations.base-url:https://api.funtranslations.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public String translateToYoda(String text) {
        return translate(text, "yoda");
    }

    @Override
    public String translateToShakespeare(String text) {
        return translate(text, "shakespeare");
    }

    private String translate(String text, String translator) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("translate", translator + ".json")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("text", text), headers);

        try {
            ResponseEntity<TranslationContract> response =
                    restTemplate.postForEntity(url, request, TranslationContract.class);
            TranslationContract body = response.getBody();
            if (body == null || body.contents == null || body.contents.translated == null) {
                throw new TranslationUnavailableException("Empty translation response", null);
            }
            return body.contents.translated;
        } catch (RestClientException ex) {
            throw new ExternalServiceException("FunTranslation client error:" , ex);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TranslationContract {
        public Contents contents;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Contents {
            @JsonProperty("translated")
            public String translated;
        }
    }
}
