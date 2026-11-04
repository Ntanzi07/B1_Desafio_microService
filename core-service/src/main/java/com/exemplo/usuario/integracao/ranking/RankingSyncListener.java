package com.exemplo.usuario.integracao.ranking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// Quem de fato chama o Ranking.
// AFTER_COMMIT: so envia DEPOIS que o banco do Core confirmou a transacao.
// Se a conclusao da matricula der erro e for desfeita (rollback), o Ranking nunca fica sabendo.
// Sem isso, o Ranking poderia mostrar pontos de algo que nem foi salvo no Core.
@Component
public class RankingSyncListener {

    private static final Logger log = LoggerFactory.getLogger(RankingSyncListener.class);

    private final RankingClient client;

    public RankingSyncListener(RankingClient client) {
        this.client = client;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoAlterarDados(RankingDadosAlteradosEvent e) {
        try {
            client.enviar(e);
            log.info("Ranking atualizado para usuario {}", e.usuarioId());
        } catch (Exception ex) {
            // Decisao consciente (registrar no ADR): se o Ranking estiver fora do ar,
            // o Core CONTINUA funcionando; a matricula e concluida normalmente.
            // Custo: o ranking fica desatualizado ate a proxima mudanca desse usuario.
            // Reenvio automatico/fila (consistencia eventual garantida) fica para o Desafio 4.
            log.warn("Nao foi possivel atualizar o ranking do usuario {}: {}", e.usuarioId(), ex.getMessage());
        }
    }
}
