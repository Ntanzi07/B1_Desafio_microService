# Plataforma de Gamificação — B1 Architecture Lab

Core em Spring Boot e **Ranking extraído como serviço com banco próprio** (Desafio 1,
alternativa C — decomposição seletiva).

| | |
|---|---|
| Equipe | Amanda Soares - 235276; Giulia Albuquerque - 224643; Marisol Marques - 222634; Nathan Tanzi - 223202; |
| **Registro arquitetural** (ASRs, ADRs, C4, evidências) | [`docs/arquitetura/README.md`](docs/arquitetura/README.md) |
| Evidência do Desafio 1 | [`docs/evidencias/desafio-1.md`](docs/evidencias/desafio-1.md) |


## Como executar

Pré-requisito: Docker Desktop.

1. Na raiz, crie o `.env` a partir do exemplo e defina a credencial de serviço:
   ```
   copy .env.example .env
   ```
2. Suba tudo:
   ```
   docker compose up --build -d
   ```

| Serviço | Endereço | Acesso |
|---|---|---|
| Core (front) | http://localhost:8080/index.html | HTTP Basic `admin` / `123456` |
| Core (Swagger) | http://localhost:8080/swagger-ui.html | `admin` / `123456` |
| Ranking (Swagger) | http://localhost:8081/swagger-ui.html | GET público; PUT/DELETE com a credencial do `.env` |
| pgAdmin do Core | http://localhost:5050 | `admin@admin.com` / `admin` — servidor `core-postgres:5432`, banco `core_db` |
| pgAdmin do Ranking | http://localhost:5051 | `admin@admin.com` / `admin` — servidor `ranking-postgres:5432`, banco `ranking_db` |

Estrutura:

```
core-service/       Core (usuario, curso, matricula, integracao/ranking)
ranking-service/    Ranking Service (read model do ranking)
docs/evidencias/    Roteiro e resultados do mini-lab
docker-compose.yml  Sobe os 2 serviços, 2 PostgreSQL e 2 pgAdmin
.env.example        Variáveis necessárias (o .env real não é versionado)
```

## Testes

Sem precisar de Java/Maven instalados (PowerShell, na raiz):

```
docker run --rm -v "${PWD}\ranking-service:/app" -v maven_repo:/root/.m2 -w /app maven:3.9.9-eclipse-temurin-17 mvn clean test
docker run --rm -v "${PWD}\core-service:/app" -v maven_repo:/root/.m2 -w /app maven:3.9.9-eclipse-temurin-17 mvn clean test
```

| Serviço | Testes | Cobrem |
|---|---|---|
| Ranking | 23 | domínio (pontos), upsert, segurança 401, PUT idempotente, 400/404, CORS |
| Core | 16 | regras de nota/moedas, publicação do evento ao Ranking, falha do Ranking não derruba o Core |
