# usuario-api-v2

Evolução didática da aplicação Spring Boot, agora preparada para **PostgreSQL + Docker**.

## O que mudou

- a aplicação continua com a mesma estrutura didática de domínio, DTO, service, repository e controller
- foi adicionada dependência do **PostgreSQL**
- a configuração foi separada por **profiles**
- foi criado um **Dockerfile** para empacotar a API
- foi criado um **docker-compose.yml** para subir **aplicação + banco PostgreSQL + pgAdmin**
- o profile padrão passou a ser `postgres`
- o profile `h2` foi mantido apenas como apoio didático/local

## Profiles disponíveis

### 1) Profile padrão: PostgreSQL
A aplicação sobe usando PostgreSQL por padrão.

Principais variáveis:

- `SPRING_PROFILES_ACTIVE=postgres`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/usuario_db`
- `SPRING_DATASOURCE_USERNAME=postgres`
- `SPRING_DATASOURCE_PASSWORD=postgres`

### 2) Profile opcional: H2
Se quiser voltar ao banco em memória apenas para estudo local:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

## Como executar com Docker

Na raiz do projeto:

```bash
docker compose up --build
```

Isso sobe:

- API Spring Boot em `http://localhost:8080`
- PostgreSQL em `localhost:5432`
- pgAdmin em `http://localhost:5050`

## Como executar sem Docker

### Opção A: usando PostgreSQL local

```bash
mvn spring-boot:run
```

### Opção B: usando H2 apenas para estudo

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

## Acessos

- Front simples: `http://localhost:8080/index.html`
- Home MVC: `http://localhost:8080/home`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

## Login HTTP Basic

- usuário: `admin`
- senha: `123456`

## Dados do PostgreSQL no Docker Compose

- Banco: `usuario_db`
- Usuário: `postgres`
- Senha: `postgres`
- Porta: `5432`

## Acesso ao pgAdmin

Acesse:

- `http://localhost:5050`

Login do pgAdmin:

- Email: `admin@admin.com`
- Senha: `admin`

### Como registrar o servidor no pgAdmin

Ao abrir o pgAdmin pela primeira vez, cadastre uma nova conexão com estes dados:

- Name: `Postgres Docker`
- Host name/address: `postgres`
- Port: `5432`
- Maintenance database: `usuario_db`
- Username: `postgres`
- Password: `postgres`

> Importante: dentro do Docker Compose, o host do banco é `postgres`, porque esse é o nome do serviço e também o hostname fixado no compose. Não use `db` nem `localhost` dentro do pgAdmin.

### Como ver as tabelas

No pgAdmin:

1. Servers
2. Postgres Docker
3. Databases
4. usuario_db
5. Schemas
6. public
7. Tables

Você também pode abrir o **Query Tool** e executar, por exemplo:

```sql
SELECT * FROM usuarios;
SELECT * FROM cursos;
SELECT * FROM matriculas;
```

## Fluxo sugerido para teste

1. Criar usuário
2. Criar curso
3. Matricular usuário em curso normal
4. Concluir matrícula com nota 8.0
5. Consultar usuário e observar os 3 créditos
6. Criar matrícula bônus e ver o consumo de crédito

## Estrutura adicionada para Docker

- `Dockerfile` -> gera a imagem da aplicação
- `.dockerignore` -> evita enviar arquivos desnecessários para o build
- `docker-compose.yml` -> sobe aplicação + PostgreSQL + pgAdmin

## Build manual da imagem Docker

```bash
docker build -t seuusuario/usuario-api-v2:1.0.0 .
```

## Publicação no Docker Hub

```bash
docker login
docker push seuusuario/usuario-api-v2:1.0.0
```

## Versão com Value Objects

Nesta revisão, `Usuario` e `Curso` continuam encapsulando seus atributos principais com Value Objects:

- `NomeUsuario`
- `EmailUsuario`
- `SenhaCriptografada`
- `TituloCurso`
- `DescricaoCurso`

### Impacto no front-end e controllers

Nenhum payload REST foi alterado.

Os endpoints continuam aceitando e retornando JSON com os mesmos campos:

- `nome`
- `email`
- `senha`
- `titulo`
- `descricao`

Por isso, o `frontend-vue/index.html` e os controllers permanecem compatíveis.


## Se a conexão do pgAdmin falhar

Execute nesta ordem:

```bash
docker compose down -v
docker compose up --build
```

Depois, no pgAdmin, cadastre o servidor com:

- Host name/address: `postgres`
- Port: `5432`
- Database: `usuario_db`
- Username: `postgres`
- Password: `postgres`


## B1 – Desafio 1
Leia `README-B1-DESAFIO1.md` para a decisão arquitetural e comparação didática.
