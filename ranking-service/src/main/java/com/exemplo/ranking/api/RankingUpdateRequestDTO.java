package com.exemplo.ranking.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Corpo do PUT que o Core envia.
// Nao tem usuarioId nem pontos:
// - usuarioId vem na URL (/api/ranking/{usuarioId})
// - pontos e calculado AQUI, para o Core nao poder mandar um valor errado
// As anotacoes sao validadas pelo @Valid no controller (spring-boot-starter-validation).
public record RankingUpdateRequestDTO(
        @NotBlank String nome,
        @NotNull @Min(0) Integer cursosConcluidos,
        @NotNull @Min(0) Integer moedas
) {
}
