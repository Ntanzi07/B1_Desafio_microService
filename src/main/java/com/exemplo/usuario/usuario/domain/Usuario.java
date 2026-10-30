package com.exemplo.usuario.usuario.domain;

import com.exemplo.usuario.usuario.domain.EmailUsuario;
import com.exemplo.usuario.usuario.domain.NomeUsuario;
import com.exemplo.usuario.usuario.domain.SenhaCriptografada;
import jakarta.persistence.*;

// Camada: DOMINIO.
// Usuario e uma entidade do negocio.
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome, e-mail e senha foram encapsulados como Value Objects.
    @Embedded
    private NomeUsuario nome;

    @Embedded
    private EmailUsuario email;

    @Embedded
    private SenhaCriptografada senha;

    // Relacao 1:1 com Assinatura.
    // mappedBy = a outra entidade (Assinatura) possui a FK.
    // cascade = ALL faz persistencia em cascata.
    // orphanRemoval = remove a assinatura se ela deixar de estar vinculada.
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Assinatura assinatura;

    protected Usuario() {
    }

    // Construtor rico do dominio.
    // Repare que a senha recebida aqui ja deve estar criptografada pela camada de service.
    public Usuario(String nome, String email, String senhaCriptografada) {
        this.nome = new NomeUsuario(nome);
        this.email = new EmailUsuario(email);
        this.senha = new SenhaCriptografada(senhaCriptografada);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome.getValor();
    }

    public String getEmail() {
        return email.getValor();
    }

    public String getSenha() {
        return senha.getValor();
    }

    public Assinatura getAssinatura() {
        return assinatura;
    }

    // Alteracoes controladas do estado da entidade.
    public void alterarNome(String nome) {
        this.nome = new NomeUsuario(nome);
    }

    public void alterarEmail(String email) {
        this.email = new EmailUsuario(email);
    }

    public void alterarSenhaCriptografada(String senhaCriptografada) {
        this.senha = new SenhaCriptografada(senhaCriptografada);
    }

    // Mantem a consistencia da associacao bidirecional Usuario <-> Assinatura.
    public void vincularAssinatura(Assinatura assinatura) {
        this.assinatura = assinatura;
        if (assinatura != null && assinatura.getUsuario() != this) {
            assinatura.setUsuario(this);
        }
    }
}
