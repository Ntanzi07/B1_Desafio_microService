package com.exemplo.usuario.usuario.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Teste de DOMINIO da Assinatura: cursos, creditos, moedas e promocao para PREMIUM.
class AssinaturaTest {

    private Assinatura nova() {
        return new Assinatura(new Usuario("Ana", "ana@email.com", "hash"));
    }

    @Test
    void nasceBasicoZerada() {
        Assinatura a = nova();
        assertEquals(PlanoAssinatura.BASICO, a.getPlano());
        assertEquals(0, a.getCursosConcluidosComSucesso());
        assertEquals(0, a.getMoedas());
        assertEquals(0, a.getCreditosCursos());
    }

    @Test
    void conclusaoSomaCursoEDaTresCreditos() {
        Assinatura a = nova();
        a.registrarConclusaoComSucesso();
        assertEquals(1, a.getCursosConcluidosComSucesso());
        assertEquals(3, a.getCreditosCursos());
    }

    @Test
    void dozeConclusoesPromovemParaPremium() {
        Assinatura a = nova();
        for (int i = 0; i < 11; i++) a.registrarConclusaoComSucesso();
        assertEquals(PlanoAssinatura.BASICO, a.getPlano());

        a.registrarConclusaoComSucesso();
        assertEquals(PlanoAssinatura.PREMIUM, a.getPlano());
    }

    @Test
    void adicionarMoedasAcumula() {
        Assinatura a = nova();
        a.adicionarMoedas(50);
        a.adicionarMoedas(50);
        assertEquals(100, a.getMoedas());
    }

    @Test
    void consumirCreditoSemSaldoLancaExcecao() {
        assertThrows(IllegalStateException.class, () -> nova().consumirCredito());
    }
}
