package com.exemplo.usuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication e a anotacao principal do Spring Boot.
// Ela equivale, na pratica, a combinar tres ideias importantes:
// 1) @Configuration -> esta classe tambem pode registrar beans/configuracoes.
// 2) @EnableAutoConfiguration -> o Spring tenta configurar muita coisa automaticamente.
// 3) @ComponentScan -> o Spring varre os pacotes abaixo deste pacote procurando classes anotadas.
//
// Como esta classe esta em com.exemplo.usuario, o Spring vai localizar automaticamente:
// - controllers
// - services
// - repositories
// - configs
// - outros componentes dentro deste pacote e subpacotes
@SpringBootApplication
public class UsuarioApplication {

    // Metodo main = ponto de entrada padrao de uma aplicacao Java.
    // Quando executamos este metodo, o Spring Boot sobe toda a aplicacao.
    public static void main(String[] args) {
        // SpringApplication.run(...) cria o contexto Spring (container).
        // E aqui que acontece a Inversao de Controle (IoC):
        // em vez de voce criar manualmente controller/service/repository com new,
        // o Spring cria os objetos (beans), conecta as dependencias e administra o ciclo de vida.
        SpringApplication.run(UsuarioApplication.class, args);
    }
}
