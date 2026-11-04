# Evidência — Desafio 1 (Ranking como serviço)

Hipótese testada: **o Ranking pode viver em outro processo, com banco próprio, sem que o
Core dependa dele para funcionar — ao custo de consistência eventual.**

Pré-requisito: `.env` na raiz (copiar de `.env.example`) e `docker compose up --build -d`.

| # | Teste | Como | Esperado | Resultado observado |
|---|---|---|---|---|
| 1 | Os 2 serviços sobem separados | `docker ps` | `core-api`, `ranking-api`, `core-postgres`, `ranking-postgres` em pé | _print_ |
| 2 | Novo usuário entra no ranking | Criar usuário no front (`:8080/index.html`) e clicar em "Atualizar ranking" | Usuário com 0 pontos | _print_ |
| 3 | Conclusão atualiza o ranking | Matricular e concluir com nota 10 | 150 pontos (100 + 50 moedas) | _print_ |
| 4 | PUT exige credencial | `PUT /api/ranking/1` no Swagger `:8081` sem Authorize | HTTP 401 | _print_ |
| 5 | PUT é idempotente | Mesmo PUT (autorizado) duas vezes | Mesma linha, mesmos pontos | _print_ |
| 6 | Falha isolada | `docker stop ranking-api` e concluir outra matrícula | Core responde 200; log do Core mostra `Nao foi possivel atualizar o ranking` | _print + log_ |
| 7 | Consistência eventual | `docker start ranking-api` e consultar o ranking | Usuário do teste 6 com pontos **antigos** (mudança perdida) | _print_ |

Comandos úteis:

```
docker compose logs core --tail 30
docker stop ranking-api
docker start ranking-api
curl.exe http://localhost:8081/api/ranking
```

Conclusão: _preencher após os testes. O teste 7 é o motivo do Desafio 4._
