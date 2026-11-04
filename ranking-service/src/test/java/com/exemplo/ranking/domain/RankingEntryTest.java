package com.exemplo.ranking.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Teste de DOMINIO puro: sem Spring, sem banco, roda em milissegundos.
// Prova que a regra de pontuacao mora na entidade e e a mesma do monolito.
class RankingEntryTest {

    @Test
    void calculaPontosComoCemPorCursoMaisMoedas() {
        RankingEntry entry = new RankingEntry(1L);

        entry.atualizar("Ana", 2, 30);

        assertEquals(230, entry.getPontos());
        assertEquals("Ana", entry.getNome());
        assertNotNull(entry.getAtualizadoEm());
    }

    @Test
    void atualizarDeNovoSubstituiOsValoresEmVezDeSomar() {
        // Base da idempotencia do PUT: o Core manda o ESTADO, nao um incremento.
        RankingEntry entry = new RankingEntry(1L);
        entry.atualizar("Ana", 2, 30);

        entry.atualizar("Ana", 2, 30);

        assertEquals(230, entry.getPontos());
    }

    @Test
    void removeEspacosDoNome() {
        RankingEntry entry = new RankingEntry(1L);

        entry.atualizar("  Ana  ", 0, 0);

        assertEquals("Ana", entry.getNome());
    }

    @Test
    void rejeitaUsuarioIdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new RankingEntry(null));
        assertThrows(IllegalArgumentException.class, () -> new RankingEntry(0L));
        assertThrows(IllegalArgumentException.class, () -> new RankingEntry(-5L));
    }

    @Test
    void rejeitaNomeVazio() {
        RankingEntry entry = new RankingEntry(1L);

        assertThrows(IllegalArgumentException.class, () -> entry.atualizar(null, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> entry.atualizar("   ", 1, 1));
    }

    @Test
    void rejeitaValoresNegativos() {
        RankingEntry entry = new RankingEntry(1L);

        assertThrows(IllegalArgumentException.class, () -> entry.atualizar("Ana", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> entry.atualizar("Ana", 0, -1));
    }
}
