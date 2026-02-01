package com.example.pokedex.domain.ports;

public interface TranslationProvider {
    String translateToYoda(String text);

    String translateToShakespeare(String text);
}
