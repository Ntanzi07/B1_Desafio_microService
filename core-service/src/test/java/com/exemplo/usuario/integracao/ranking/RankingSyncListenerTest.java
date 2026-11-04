package com.exemplo.usuario.integracao.ranking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

// Evidencia da decisao "Ranking fora do ar NAO derruba o Core":
// se a chamada HTTP falhar, o listener registra no log e segue, sem propagar a excecao.
@ExtendWith(MockitoExtension.class)
class RankingSyncListenerTest {

    @Mock
    private RankingClient client;

    @InjectMocks
    private RankingSyncListener listener;

    private final RankingDadosAlteradosEvent evento = new RankingDadosAlteradosEvent(10L, "Ana", 1, 50);

    @Test
    void enviaOEventoAoRanking() {
        listener.aoAlterarDados(evento);

        verify(client).enviar(evento);
    }

    @Test
    void falhaDoRankingNaoPropagaParaOCore() {
        // Simula o Ranking fora do ar (conexao recusada / timeout).
        doThrow(new ResourceAccessException("Connection refused")).when(client).enviar(evento);

        assertDoesNotThrow(() -> listener.aoAlterarDados(evento));
        verify(client).enviar(evento);
    }
}
