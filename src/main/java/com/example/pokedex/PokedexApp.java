package com.example.pokedex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PokedexApp {
    public static void main(String[] args) {
        SpringApplication.run(PokedexApp.class, args);
    }
}
