package com.exemplo.usuario.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Camada: CONFIGURACAO.
// Classes de configuracao ensinam o Spring a montar partes da aplicacao.
// Aqui estamos configurando a documentacao OpenAPI / Swagger.
@Configuration
public class OpenApiConfig {

    // @Bean indica ao Spring que o objeto retornado por este metodo
    // deve ser registrado dentro do container.
    // Depois disso, o Spring passa a gerenciar esse objeto.
    @Bean
    public OpenAPI customOpenAPI() {
        // OpenAPI e o objeto que representa a documentacao da API.
        // O Swagger UI le essas informacoes para montar a tela de documentacao.
        return new OpenAPI().info(new Info()
                .title("API Usuarios V2")
                .version("2.0")
                .description("Evolucao didatica com Usuario, Assinatura, Curso e Matricula")
                .contact(new Contact().name("Exemplo Didatico")));
    }
}
