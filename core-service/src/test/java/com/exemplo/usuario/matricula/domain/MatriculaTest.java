package com.exemplo.usuario.matricula.domain;

import com.exemplo.usuario.curso.domain.Curso;
import com.exemplo.usuario.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Teste de DOMINIO: regras de aproveitamento (nota >= 7) e de nota maxima (10 -> moedas).
class MatriculaTest {

    private Matricula concluidaCom(Double nota) {
        Matricula m = new Matricula(new Usuario("Ana", "ana@email.com", "hash"), new Curso("Java", "Basico"), false);
        m.setStatus(StatusMatricula.CONCLUIDO);
        m.setNotaFinal(nota);
        return m;
    }

    @Test
    void novaMatriculaNasceEmAndamento() {
        Matricula m = new Matricula(new Usuario("Ana", "ana@email.com", "hash"), new Curso("Java", "Basico"), false);
        assertEquals(StatusMatricula.EM_ANDAMENTO, m.getStatus());
        assertFalse(m.concluidoComAproveitamento());
    }

    @Test
    void notaSeteEhAproveitamentoMasNaoEhNotaMaxima() {
        Matricula m = concluidaCom(7.0);
        assertTrue(m.concluidoComAproveitamento());
        assertFalse(m.concluidoComNotaMaxima());
    }

    @Test
    void notaAbaixoDeSeteNaoTemAproveitamento() {
        Matricula m = concluidaCom(6.9);
        assertFalse(m.concluidoComAproveitamento());
        assertFalse(m.concluidoComNotaMaxima());
    }

    @Test
    void notaDezEhNotaMaxima() {
        Matricula m = concluidaCom(10.0);
        assertTrue(m.concluidoComAproveitamento());
        assertTrue(m.concluidoComNotaMaxima());
    }

    @Test
    void semNotaNaoTemAproveitamento() {
        assertFalse(concluidaCom(null).concluidoComAproveitamento());
        assertFalse(concluidaCom(null).concluidoComNotaMaxima());
    }

    @Test
    void notaDezSemEstarConcluidaNaoContaComoNotaMaxima() {
        Matricula m = concluidaCom(10.0);
        m.setStatus(StatusMatricula.EM_ANDAMENTO);
        assertFalse(m.concluidoComNotaMaxima());
    }
}
