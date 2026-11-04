package com.exemplo.ranking.api;

import com.exemplo.ranking.domain.RankingEntry;

// Formato de saida. Os campos sao os mesmos do GET /api/ranking do monolito,
// entao quem consumia o ranking antes nao precisa mudar nada.
public record RankingResponseDTO(
        Long usuarioId,
        String nome,
        Integer cursosConcluidos,
        Integer moedas,
        Integer pontos
) {
    public static RankingResponseDTO from(RankingEntry e) {
        return new RankingResponseDTO(
                e.getUsuarioId(), e.getNome(), e.getCursosConcluidos(), e.getMoedas(), e.getPontos());
    }
}
