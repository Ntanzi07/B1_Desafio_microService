package com.exemplo.ranking.application;

// Excecao especifica: o ApiExceptionHandler transforma em HTTP 404.
public class RankingNaoEncontradoException extends RuntimeException {
    public RankingNaoEncontradoException(Long usuarioId) {
        super("Usuario " + usuarioId + " nao esta no ranking");
    }
}
