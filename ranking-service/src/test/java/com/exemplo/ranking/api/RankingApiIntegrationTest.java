package com.exemplo.ranking.api;

import com.exemplo.ranking.internal.persistence.RankingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Teste de INTEGRACAO: sobe o Ranking Service inteiro (controller, security, service, JPA)
// com H2 no lugar do PostgreSQL. Cada teste e uma evidencia de uma decisao do ADR-001.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RankingApiIntegrationTest {

    private static final String CREDENCIAL_VALIDA = basic("test-service", "test-pass");
    private static final String CREDENCIAL_ERRADA = basic("test-service", "senha-errada");
    private static final String CORPO = """
            {"nome":"Ana","cursosConcluidos":2,"moedas":30}
            """;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RankingRepository repository;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    // ---------- leitura publica ----------

    @Test
    void getDoRankingEPublico() throws Exception {
        mvc.perform(get("/api/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getDeUsuarioForaDoRankingDevolve404() throws Exception {
        mvc.perform(get("/api/ranking/99"))
                .andExpect(status().isNotFound());
    }

    // ---------- seguranca: so o servico autorizado escreve ----------

    @Test
    void putSemCredencialDevolve401() throws Exception {
        mvc.perform(put("/api/ranking/1").contentType(MediaType.APPLICATION_JSON).content(CORPO))
                .andExpect(status().isUnauthorized());
        assertEquals(0, repository.count());
    }

    @Test
    void putComSenhaErradaDevolve401() throws Exception {
        mvc.perform(put("/api/ranking/1")
                        .header(HttpHeaders.AUTHORIZATION, CREDENCIAL_ERRADA)
                        .contentType(MediaType.APPLICATION_JSON).content(CORPO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteSemCredencialDevolve401() throws Exception {
        mvc.perform(delete("/api/ranking/1"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- contrato PUT idempotente ----------

    @Test
    void putAutorizadoCriaLinhaECalculaPontos() throws Exception {
        mvc.perform(put("/api/ranking/1")
                        .header(HttpHeaders.AUTHORIZATION, CREDENCIAL_VALIDA)
                        .contentType(MediaType.APPLICATION_JSON).content(CORPO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.pontos").value(230));
    }

    @Test
    void putRepetidoNaoDuplicaNemSomaPontos() throws Exception {
        for (int i = 0; i < 3; i++) {
            mvc.perform(put("/api/ranking/1")
                            .header(HttpHeaders.AUTHORIZATION, CREDENCIAL_VALIDA)
                            .contentType(MediaType.APPLICATION_JSON).content(CORPO))
                    .andExpect(status().isOk());
        }

        assertEquals(1, repository.count());
        mvc.perform(get("/api/ranking/1"))
                .andExpect(jsonPath("$.pontos").value(230));
    }

    @Test
    void putComCorpoInvalidoDevolve400() throws Exception {
        String invalido = """
                {"nome":"","cursosConcluidos":-1,"moedas":null}
                """;
        mvc.perform(put("/api/ranking/1")
                        .header(HttpHeaders.AUTHORIZATION, CREDENCIAL_VALIDA)
                        .contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").exists())
                .andExpect(jsonPath("$.cursosConcluidos").exists())
                .andExpect(jsonPath("$.moedas").exists());
    }

    @Test
    void rankingVemOrdenadoPorPontos() throws Exception {
        salvar(1L, "{\"nome\":\"Ana\",\"cursosConcluidos\":1,\"moedas\":0}");
        salvar(2L, "{\"nome\":\"Bia\",\"cursosConcluidos\":2,\"moedas\":50}");
        salvar(3L, "{\"nome\":\"Caio\",\"cursosConcluidos\":0,\"moedas\":10}");

        mvc.perform(get("/api/ranking"))
                .andExpect(jsonPath("$[0].nome").value("Bia"))   // 250
                .andExpect(jsonPath("$[1].nome").value("Ana"))   // 100
                .andExpect(jsonPath("$[2].nome").value("Caio")); // 10
    }

    @Test
    void deleteAutorizadoEIdempotente() throws Exception {
        salvar(1L, CORPO);

        mvc.perform(delete("/api/ranking/1").header(HttpHeaders.AUTHORIZATION, CREDENCIAL_VALIDA))
                .andExpect(status().isNoContent());
        // Apagar de novo o que ja nao existe tambem e 204, nao erro.
        mvc.perform(delete("/api/ranking/1").header(HttpHeaders.AUTHORIZATION, CREDENCIAL_VALIDA))
                .andExpect(status().isNoContent());

        assertEquals(0, repository.count());
    }

    // ---------- CORS: so o front do Core le pelo navegador ----------

    @Test
    void corsLiberaOFrontDoCore() throws Exception {
        mvc.perform(get("/api/ranking").header(HttpHeaders.ORIGIN, "http://localhost:8080"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:8080"));
    }

    @Test
    void corsBloqueiaOutraOrigem() throws Exception {
        mvc.perform(get("/api/ranking").header(HttpHeaders.ORIGIN, "http://site-malicioso.com"))
                .andExpect(status().isForbidden());
    }

    // ---------- auxiliares ----------

    private void salvar(Long id, String corpo) throws Exception {
        mvc.perform(put("/api/ranking/" + id)
                        .header(HttpHeaders.AUTHORIZATION, CREDENCIAL_VALIDA)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk());
    }

    private static String basic(String user, String password) {
        String token = Base64.getEncoder()
                .encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }
}
