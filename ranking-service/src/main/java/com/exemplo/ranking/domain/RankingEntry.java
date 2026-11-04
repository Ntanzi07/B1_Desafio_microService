package com.exemplo.ranking.domain;

import jakarta.persistence.*;

import java.time.Instant;

// Camada: DOMINIO.
// Uma linha do ranking = a "copia" que o Ranking Service guarda de um usuario do Core.
// Isso e um READ MODEL: so tem o que o ranking precisa, ja no formato de leitura.
// O Core continua sendo o dono dos dados de verdade (Usuario, Assinatura);
// aqui fica uma projecao, atualizada pelo Core via PUT.
@Entity
@Table(
    name = "ranking_entries",
    // Indice em pontos: o GET ordena por esse campo, e e a consulta mais frequente.
    indexes = @Index(name = "idx_ranking_pontos", columnList = "pontos")
)
public class RankingEntry {

    // O @Id e o MESMO id do usuario no Core, e NAO e gerado aqui (sem @GeneratedValue).
    // E isso que permite o PUT /api/ranking/{usuarioId} ser idempotente:
    // o Core sempre sabe qual linha esta criando ou atualizando.
    @Id
    private Long usuarioId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer cursosConcluidos;

    @Column(nullable = false)
    private Integer moedas;

    // Pontos sao calculados e GRAVADOS, em vez de calculados na consulta.
    // Assim o banco consegue ordenar usando o indice. Troca-se um pouco de espaco
    // por leitura rapida, que e justamente o ASR de escalabilidade do Ranking.
    @Column(nullable = false)
    private Integer pontos;

    // Quando o Core mandou a ultima atualizacao. Ajuda a depurar dado desatualizado.
    @Column(nullable = false)
    private Instant atualizadoEm;

    // Construtor vazio exigido pelo JPA (reflection).
    protected RankingEntry() {
    }

    public RankingEntry(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("usuarioId invalido");
        }
        this.usuarioId = usuarioId;
    }

    // Metodo de dominio: a regra de pontuacao mora aqui, num lugar so.
    // Era a mesma formula do monolito: 100 por curso concluido + moedas.
    public void atualizar(String nome, int cursosConcluidos, int moedas) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome e obrigatorio");
        }
        if (cursosConcluidos < 0 || moedas < 0) {
            throw new IllegalArgumentException("cursosConcluidos e moedas nao podem ser negativos");
        }
        this.nome = nome.trim();
        this.cursosConcluidos = cursosConcluidos;
        this.moedas = moedas;
        this.pontos = cursosConcluidos * 100 + moedas;
        this.atualizadoEm = Instant.now();
    }

    public Long getUsuarioId() { return usuarioId; }
    public String getNome() { return nome; }
    public Integer getCursosConcluidos() { return cursosConcluidos; }
    public Integer getMoedas() { return moedas; }
    public Integer getPontos() { return pontos; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
}
