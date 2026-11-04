package com.exemplo.ranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Ponto de entrada do Ranking Service: um processo Spring Boot SEPARADO do Core.
// O @ComponentScan embutido em @SpringBootApplication varre com.exemplo.ranking
// e todos os subpacotes (config, api, application...).
@SpringBootApplication
public class RankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(RankingApplication.class, args);
    }
}
