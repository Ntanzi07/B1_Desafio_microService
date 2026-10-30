# B1 – Desafio 1 – Versão B: Monólito Modular

## Objetivo
Evoluir a mesma aplicação-base e o mesmo RF de Ranking, agora reorganizando o código por **capacidade de negócio**, sem criar microsserviços. Continua sendo **um processo Spring Boot, um banco PostgreSQL e um deploy**.

## Decisão do Tech Lead
**RF:** consultar ranking de usuários.  
**RNFs:** manutenibilidade, testabilidade, desempenho e evolução independente do código.  
**ASRs:** mudanças em Ranking não devem espalhar alterações pelo Core; crescimento desigual do Ranking; necessidade de fronteiras mais claras antes de qualquer decomposição futura.  
**RPCs:** Java 17, Spring Boot, PostgreSQL e Docker; um único processo/deploy; compatibilidade dos endpoints existentes.  
**Alternativas:** manter camadas horizontais, organizar módulos por feature, separar Ranking em serviço.  
**Decisão/ADR:** adotar Monólito Modular como passo evolutivo, sem custo operacional de sistema distribuído.  
**Trade-off:** melhora coesão e compreensão das fronteiras, mas não oferece escala/deploy independente em runtime.

## Módulos
- `usuario`: API, application, domain, internal/persistence
- `curso`: API, application, domain, internal/persistence
- `matricula`: API, application, domain, internal/persistence
- `ranking`: API, application, internal/persistence
- `shared`: configuração e suporte web transversal

## C4 – Container (texto)
Usuário -> API Spring Boot [módulos Usuario | Curso | Matrícula | Ranking] -> PostgreSQL. Os módulos são fronteiras internas do mesmo container.

## Evidência prática
O endpoint permanece `GET /api/ranking`. A funcionalidade está fisicamente agrupada em `ranking/`, facilitando localizar API, aplicação e persistência relacionadas.

## Como executar
`docker compose up --build`

Swagger: `http://localhost:8080/swagger-ui/index.html` (admin/123456).

## Comparação esperada no B1
A equipe deve comparar esta versão com a versão em camadas e justificar, usando RF/RNF/ASR/RPC, se a melhoria de modularidade compensa a reorganização. **Nenhuma das duas versões é automaticamente a resposta correta.**
