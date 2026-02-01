# Pokedex REST API

Small Spring Boot REST API that exposes Pokemon information and a fun translated description.
It integrates with the public PokéAPI and Fun Translations API and uses caching to reduce
external calls.

## Requirements

- Java 21 (JDK)
- Maven 3.9+

## How to run

### Local (Maven)

```bash
mvn spring-boot:run
```

The service listens on `http://localhost:8000`.

Example requests:

```bash
curl http://localhost:8000/pokemon/mewtwo
curl http://localhost:8000/pokemon/translated/mewtwo
```

### Tests

```bash
mvn test
```

## Docker

Build and run:

```bash
docker build -t pokedex .
docker run -p 8000:8000 pokedex
```

## Configuration

Override these properties via environment variables or `application.yml`:

- `pokedex.pokeapi.base-url`
- `pokedex.funtranslations.base-url`
