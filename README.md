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

## Production notes / design decisions

- External client url should be moved to a config file in production
- External calls are cached in-memory using Spring's simple cache.
  For production, replace with a distributed cache (Redis) and a proper eviction policy.
- This app does not implement retries, circuit breakers, or rate limiting.
  For production, add resiliency (e.g. Resilience4j), per-endpoint timeouts, and a fallback strategy.
- Observability is minimal. In production, add structured logs, metrics, and tracing.
- Project Structure reflects Domain-Driven Design approach to decouple external services from application business logic
- Final version has been reached iterating over the requirements and following Test-Driven Development methodologies


## AI Usage

For this project I was assisted by ChatGPT 5.2 Codex to implement the base structure of the Spring-Boot application, pom.xml, Dockerfile and finally the README