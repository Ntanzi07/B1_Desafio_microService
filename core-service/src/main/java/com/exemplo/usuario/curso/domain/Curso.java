package com.exemplo.usuario.curso.domain;

import com.exemplo.usuario.curso.domain.DescricaoCurso;
import com.exemplo.usuario.curso.domain.TituloCurso;
import jakarta.persistence.*;

// Camada: DOMINIO.
// Curso usa Value Objects para encapsular titulo e descricao.
@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Embedded embute o Value Object dentro da tabela da entidade.
    @Embedded
    private TituloCurso titulo;

    @Embedded
    private DescricaoCurso descricao;

    // protected e suficiente para o JPA e evita uso indevido fora do dominio.
    protected Curso() {
    }

    // Construtor rico: delega validacao dos atributos para os Value Objects.
    public Curso(String titulo, String descricao) {
        this.titulo = new TituloCurso(titulo);
        this.descricao = new DescricaoCurso(descricao);
    }

    public Long getId() {
        return id;
    }

    // Repare que a entidade expõe String para fora,
    // mas internamente guarda TituloCurso.
    public String getTitulo() {
        return titulo.getValor();
    }

    public String getDescricao() {
        return descricao != null ? descricao.getValor() : null;
    }

    // Metodos de alteracao controlada do dominio.
    public void alterarTitulo(String titulo) {
        this.titulo = new TituloCurso(titulo);
    }

    public void alterarDescricao(String descricao) {
        this.descricao = new DescricaoCurso(descricao);
    }
}
