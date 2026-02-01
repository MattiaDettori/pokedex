package com.example.pokedex.infrastructure.translation;

import com.example.pokedex.application.exception.ExternalServiceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import com.example.pokedex.application.exception.TranslationUnavailableException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FunTranslationClientTest {

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
    void translatesToYoda() {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "contents": {
                            "translated": "Powerful you have become."
                          }
                        }
                        """));

        String baseUrl = server.url("/").toString();
        FunTranslationsClient client = new FunTranslationsClient(new RestTemplate(), baseUrl);

        String translated = client.translateToYoda("You have become powerful.");

        assertThat(translated).isEqualTo("Powerful you have become.");
    }

    @Test
    void translatesToShakespeare() {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "contents": {
                            "translated": "Thou art most kind."
                            }
                        }
                        """));

        String baseUrl = server.url("/").toString();
        FunTranslationsClient client = new FunTranslationsClient(new RestTemplate(), baseUrl);

        String translated = client.translateToShakespeare("You are very kind.");

        assertThat(translated).isEqualTo("Thou art most kind.");
    }

    @Test
    void throwsWhenResponseMissingTranslatedField() {
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "contents": {
                          }
                        }
                        """));

        String baseUrl = server.url("/").toString();
        FunTranslationsClient client = new FunTranslationsClient(new RestTemplate(), baseUrl);

        assertThatThrownBy(() -> client.translateToShakespeare("You are very kind."))
                .isInstanceOf(TranslationUnavailableException.class);
    }

    @Test
    void throwsWhenResponseBodyIsNull() {
        server.enqueue(new MockResponse().setResponseCode(204));

        String baseUrl = server.url("/").toString();
        FunTranslationsClient client = new FunTranslationsClient(new RestTemplate(), baseUrl);

        assertThatThrownBy(() -> client.translateToYoda("You have become powerful."))
                .isInstanceOf(TranslationUnavailableException.class);
    }

    @Test
    void throwsWhenApiReturnsError() {
        server.enqueue(new MockResponse().setResponseCode(500));

        String baseUrl = server.url("/").toString();
        FunTranslationsClient client = new FunTranslationsClient(new RestTemplate(), baseUrl);

        assertThatThrownBy(() -> client.translateToYoda("You have become powerful."))
                .isInstanceOf(ExternalServiceException.class);
    }
}
