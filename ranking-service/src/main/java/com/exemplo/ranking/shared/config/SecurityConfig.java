package com.exemplo.ranking.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// Camada: CONFIGURACAO.
// Regras do Ranking Service:
// - GET  /api/ranking/**  -> publico (qualquer pessoa ve o ranking)
// - PUT/DELETE /api/ranking/** -> so quem tem a credencial de servico (o Core)
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // API chamada por outro servico, sem formulario/cookie: CSRF nao se aplica.
            .csrf(csrf -> csrf.disable())

            // Sem sessao: cada requisicao do Core manda a credencial de novo (HTTP Basic).
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Documentacao livre para facilitar os testes.
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                // Leitura publica do ranking.
                .requestMatchers(HttpMethod.GET, "/api/ranking/**").permitAll()

                // Escrita so para o servico autorizado.
                .requestMatchers(HttpMethod.PUT, "/api/ranking/**").hasRole("SERVICE")
                .requestMatchers(HttpMethod.DELETE, "/api/ranking/**").hasRole("SERVICE")

                // Qualquer outra coisa: exige login (fechado por padrao).
                .anyRequest().authenticated())

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // Diferenca para o Core: usuario e senha NAO estao escritos no codigo.
    // @Value le as propriedades do application.properties, que por sua vez
    // leem as variaveis de ambiente vindas do .env pelo docker-compose.
    @Bean
    public UserDetailsService users(
            @Value("${ranking.security.user}") String user,
            @Value("${ranking.security.password}") String password,
            PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername(user)
                .password(passwordEncoder.encode(password))
                .roles("SERVICE")
                .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
