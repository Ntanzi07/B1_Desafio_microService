package com.exemplo.usuario.curso.api;

import jakarta.validation.constraints.NotBlank;

// DTO de entrada para criacao de curso.
public class CursoRequestDTO {

    @NotBlank(message = "Titulo e obrigatorio")
    private String titulo;

    private String descricao;

    public CursoRequestDTO() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
