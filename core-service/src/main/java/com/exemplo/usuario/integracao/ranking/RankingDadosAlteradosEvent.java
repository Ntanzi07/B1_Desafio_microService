package com.exemplo.usuario.integracao.ranking;

// Evento interno do Core: "os dados que o ranking usa mudaram para este usuario".
// E uma FOTO (snapshot) do estado no momento da mudanca, nao so o id.
// Como o PUT do Ranking recebe o estado completo e e idempotente,
// mandar a foto inteira e mais simples e seguro do que mandar "+1 curso".
public record RankingDadosAlteradosEvent(
        Long usuarioId,
        String nome,
        int cursosConcluidos,
        int moedas
) {
}
