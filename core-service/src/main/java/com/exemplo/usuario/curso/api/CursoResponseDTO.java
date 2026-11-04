package com.exemplo.usuario.curso.api;

// DTO de saida da API para curso.
public class CursoResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;

    public CursoResponseDTO() {
    }

    public CursoResponseDTO(Long id, String titulo, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }
}
