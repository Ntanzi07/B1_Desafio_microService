package com.exemplo.usuario.matricula.api;

import jakarta.validation.constraints.NotNull;

// DTO de entrada para criar matricula.
public class MatriculaRequestDTO {

    @NotNull(message = "usuarioId e obrigatorio")
    private Long usuarioId;

    @NotNull(message = "cursoId e obrigatorio")
    private Long cursoId;

    private boolean bonus;

    public MatriculaRequestDTO() {
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public boolean isBonus() {
        return bonus;
    }

    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }
}
