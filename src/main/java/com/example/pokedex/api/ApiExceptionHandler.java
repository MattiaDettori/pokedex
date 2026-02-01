package com.example.pokedex.api;

import com.example.pokedex.application.exception.ExternalServiceException;
import com.example.pokedex.application.exception.PokemonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PokemonNotFoundException.class)
    public ProblemDetail handleNotFound(PokemonNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ProblemDetail handleExternalFailure(ExternalServiceException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setDetail(ex.getMessage());
        return detail;
    }
}
