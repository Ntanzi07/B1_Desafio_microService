package com.exemplo.usuario.usuario.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

// Value Object para a senha ja criptografada.
// O dominio apenas garante que o valor existe.
// A criptografia em si acontece antes, na camada de servico, via PasswordEncoder.
@Embeddable
public class SenhaCriptografada {

    @Column(name = "senha", nullable = false)
    private String valor;

    protected SenhaCriptografada() {
    }

    public SenhaCriptografada(String valor) {
        String normalizado = valor == null ? null : valor.trim();
        if (normalizado == null || normalizado.isBlank()) {
            throw new IllegalArgumentException("Senha e obrigatoria");
        }
        this.valor = normalizado;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SenhaCriptografada that)) return false;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
