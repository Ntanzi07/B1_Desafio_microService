package com.exemplo.ranking.shared.web;

import com.exemplo.ranking.application.RankingNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

// Traduz excecoes em respostas HTTP, igual ao ApiExceptionHandler do Core.
@RestControllerAdvice
public class ApiExceptionHandler {

    // 404: GET /api/ranking/{id} de um usuario que nao esta no ranking.
    @ExceptionHandler(RankingNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(RankingNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }

    // 400: o @Valid do PUT falhou (ex.: nome vazio, moedas negativas).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> String.valueOf(f.getDefaultMessage()),
                        (a, b) -> a));
        return ResponseEntity.badRequest().body(erros);
    }

    // 400: regra do dominio violada (ex.: usuarioId <= 0).
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> argumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }
}
