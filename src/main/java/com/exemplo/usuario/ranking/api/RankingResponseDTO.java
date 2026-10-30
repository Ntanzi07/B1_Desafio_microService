package com.exemplo.usuario.ranking.api;

public class RankingResponseDTO {
    private final Long usuarioId; private final String nome; private final Integer cursosConcluidos; private final Integer moedas; private final Integer pontos;
    public RankingResponseDTO(Long usuarioId, String nome, Integer cursosConcluidos, Integer moedas, Integer pontos) { this.usuarioId=usuarioId; this.nome=nome; this.cursosConcluidos=cursosConcluidos; this.moedas=moedas; this.pontos=pontos; }
    public Long getUsuarioId(){return usuarioId;} public String getNome(){return nome;} public Integer getCursosConcluidos(){return cursosConcluidos;} public Integer getMoedas(){return moedas;} public Integer getPontos(){return pontos;}
}
