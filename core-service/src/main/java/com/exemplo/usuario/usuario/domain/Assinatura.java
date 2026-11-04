package com.exemplo.usuario.usuario.domain;

import jakarta.persistence.*;

// Camada: DOMINIO.
// Entidade JPA que representa a assinatura do usuario.
@Entity
@Table(name = "assinaturas")
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Enumerated(EnumType.STRING) grava o nome do enum no banco.
    // Ex.: BASICO, PREMIUM.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanoAssinatura plano;

    @Column(nullable = false)
    private Integer creditosCursos;

    @Column(nullable = false)
    private Integer cursosConcluidosComSucesso;

    @Column(nullable = false)
    private Integer moedas;

    // Relacionamento 1:1 com Usuario.
    // fetch = LAZY significa que o usuario sera carregado sob demanda.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    // Construtor vazio exigido pelo JPA.
    public Assinatura() {
    }

    // Construtor de negocio.
    // Todo novo usuario nasce com assinatura BASICO e zero creditos/moedas.
    public Assinatura(Usuario usuario) {
        this.plano = PlanoAssinatura.BASICO;
        this.creditosCursos = 0;
        this.cursosConcluidosComSucesso = 0;
        this.moedas = 0;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public PlanoAssinatura getPlano() {
        return plano;
    }

    public Integer getCreditosCursos() {
        return creditosCursos;
    }

    public Integer getCursosConcluidosComSucesso() {
        return cursosConcluidosComSucesso;
    }

    public Integer getMoedas() {
        return moedas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    // Estes setters existem para o JPA e para manutencao controlada da entidade.
    public void setPlano(PlanoAssinatura plano) {
        this.plano = plano;
    }

    public void setCreditosCursos(Integer creditosCursos) {
        this.creditosCursos = creditosCursos;
    }

    public void setCursosConcluidosComSucesso(Integer cursosConcluidosComSucesso) {
        this.cursosConcluidosComSucesso = cursosConcluidosComSucesso;
    }

    public void setMoedas(Integer moedas) {
        this.moedas = moedas;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Metodo de dominio: adiciona creditos.
    // Este tipo de metodo pertence ao dominio, nao ao controller.
    public void adicionarCreditos(int quantidade) {
        this.creditosCursos += quantidade;
    }

    // Metodo de dominio: consome um credito, se houver saldo.
    public void consumirCredito() {
        if (this.creditosCursos <= 0) {
            throw new IllegalStateException("Usuario sem creditos disponiveis para cursos bonus.");
        }
        this.creditosCursos--;
    }

    // Metodo de dominio rico:
    // - soma conclusao com sucesso
    // - entrega 3 creditos
    // - promove para PREMIUM ao atingir 12 conclusoes
    public void registrarConclusaoComSucesso() {
        this.cursosConcluidosComSucesso++;
        adicionarCreditos(3);
        if (this.cursosConcluidosComSucesso >= 12) {
            this.plano = PlanoAssinatura.PREMIUM;
        }
    }

    public void adicionarMoedas(int quantidade) {
        this.moedas += quantidade;
    }
}
