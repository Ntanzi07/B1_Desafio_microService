package com.exemplo.ranking.internal.persistence;

import com.exemplo.ranking.domain.RankingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Camada: REPOSITORY.
// Diferenca para o monolito: antes o RankingRepository era JpaRepository<Assinatura, Long>
// e lia a tabela do Core. Agora ele so conhece a propria tabela (ranking_entries).
public interface RankingRepository extends JpaRepository<RankingEntry, Long> {

    // Metodo derivado por nome: "ordene por pontos decrescente; em empate, por usuarioId".
    // O desempate deixa a ordem estavel entre uma chamada e outra.
    List<RankingEntry> findAllByOrderByPontosDescUsuarioIdAsc();
}
