package com.exemplo.usuario.integracao.ranking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;

// Camada: INTEGRACAO (cliente HTTP do Ranking Service).
// E a unica classe do Core que sabe que o Ranking existe e onde ele mora.
@Component
public class RankingClient {

    private final RestClient restClient;

    public RankingClient(@Value("${ranking.api.url}") String baseUrl,
                         @Value("${ranking.api.user}") String user,
                         @Value("${ranking.api.password}") String password) {
        // Timeouts curtos: se o Ranking estiver lento, o Core NAO pode ficar travado esperando.
        // Sem isso, um Ranking lento deixaria lenta a conclusao de matricula no Core.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(2));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                // HTTP Basic com a credencial de servico do .env (o Ranking exige no PUT).
                .defaultHeaders(h -> h.setBasicAuth(user, password))
                .build();
    }

    public void enviar(RankingDadosAlteradosEvent e) {
        restClient.put()
                .uri("/api/ranking/{usuarioId}", e.usuarioId())
                .contentType(MediaType.APPLICATION_JSON)
                // Mesmo formato do RankingUpdateRequestDTO do Ranking Service: e o CONTRATO entre os dois.
                .body(Map.of(
                        "nome", e.nome(),
                        "cursosConcluidos", e.cursosConcluidos(),
                        "moedas", e.moedas()))
                .retrieve()
                .toBodilessEntity();
    }
}
