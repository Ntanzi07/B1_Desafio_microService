package com.exemplo.usuario.curso.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

// Value Object do dominio.
// Nao tem identidade propria; representa apenas um valor.
@Embeddable
public class DescricaoCurso {

    @Column(name = "descricao", length = 1000)
    private String valor;

    protected DescricaoCurso() {
    }

    public DescricaoCurso(String valor) {
        // Aqui aceitamos null, pois a descricao foi modelada como opcional.
        this.valor = valor == null ? null : valor.trim();
    }

    public String getValor() {
        return valor;
    }

    // Em Value Object, equals/hashCode normalmente comparam o valor interno.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DescricaoCurso that)) return false;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
