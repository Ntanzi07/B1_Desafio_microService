package com.exemplo.usuario.matricula.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

// DTO de entrada para concluir matricula.
// DTO pertence a camada de transporte de dados.
public class ConcluirMatriculaRequestDTO {

    @NotNull(message = "notaFinal e obrigatoria")
    @DecimalMin(value = "0.0", message = "Nota minima 0")
    @DecimalMax(value = "10.0", message = "Nota maxima 10")
    private Double notaFinal;

    public ConcluirMatriculaRequestDTO() {
    }

    public Double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Double notaFinal) {
        this.notaFinal = notaFinal;
    }
}
