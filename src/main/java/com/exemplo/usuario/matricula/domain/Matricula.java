package com.exemplo.usuario.matricula.domain;

import jakarta.persistence.*;

// Camada: DOMINIO.
// Matricula liga Usuario e Curso.
@Entity
@Table(name = "matriculas")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMatricula status;

    @Column
    private Double notaFinal;

    @Column(nullable = false)
    private boolean bonus;

    public Matricula() {
    }

    // Toda nova matricula nasce EM_ANDAMENTO.
    public Matricula(Usuario usuario, Curso curso, boolean bonus) {
        this.usuario = usuario;
        this.curso = curso;
        this.bonus = bonus;
        this.status = StatusMatricula.EM_ANDAMENTO;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Curso getCurso() {
        return curso;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public Double getNotaFinal() {
        return notaFinal;
    }

    public boolean isBonus() {
        return bonus;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }

    public void setNotaFinal(Double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }

    // Metodo de dominio que encapsula a regra de aprovacao.
    public boolean concluidoComAproveitamento() {
        return StatusMatricula.CONCLUIDO.equals(this.status)
                && this.notaFinal != null
                && this.notaFinal >= 7.0;
    }
}
