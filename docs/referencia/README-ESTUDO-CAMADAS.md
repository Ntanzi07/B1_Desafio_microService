# README de estudo das camadas Spring Boot

Este arquivo foi adicionado **sem substituir o README original**.  
O `README.md` continua sendo o guia rápido do projeto.  
Este documento novo foi pensado como material de estudo.

## 1. Fluxo da aplicação

Fluxo principal da API:

```text
Cliente / Front-end / Swagger / Postman
                |
                v
           Controller
                |
                v
             Service
                |
                v
           Repository
                |
                v
             Banco H2
```

## 2. O que cada camada faz

### Controller
Responsável por:
- receber requisições HTTP;
- mapear URLs;
- receber JSON com `@RequestBody`;
- capturar parâmetros com `@PathVariable`;
- validar DTOs com `@Valid`;
- chamar a camada de service.

Anotações típicas da camada:
- `@RestController`
- `@Controller`
- `@RequestMapping`
- `@GetMapping`
- `@PostMapping`
- `@PutMapping`
- `@DeleteMapping`
- `@RequestBody`
- `@PathVariable`
- `@RequestParam`

### Service
Responsável por:
- aplicar regra de negócio;
- orquestrar entidades e repositórios;
- abrir transações;
- transformar entidade em DTO.

Anotações típicas:
- `@Service`
- `@Transactional`

### Repository
Responsável por:
- acesso ao banco;
- consultas e persistência;
- uso do Spring Data JPA.

Base típica:
- `extends JpaRepository<Entidade, Id>`

Métodos comuns:
- `findAll()`
- `findById()`
- `save()`
- `deleteById()`
- `existsBy...()`
- `findBy...()`

### Domain
Responsável por:
- representar o negócio;
- armazenar estado;
- proteger invariantes;
- concentrar comportamentos da entidade.

Anotações comuns:
- `@Entity`
- `@Table`
- `@Id`
- `@GeneratedValue`
- `@Embedded`
- `@Embeddable`
- `@OneToOne`
- `@OneToMany`
- `@ManyToOne`
- `@Enumerated`

### Config
Responsável por:
- registrar beans;
- configurar segurança;
- configurar Swagger/OpenAPI;
- ensinar o Spring a montar partes da aplicação.

Anotações comuns:
- `@Configuration`
- `@Bean`

## 3. Onde nasce a Injeção de Dependência?

A **Injeção de Dependência (DI)** vem do **Spring Container**.

No projeto, isso aparece assim:

```java
public UsuarioRestController(UsuarioService service) {
    this.service = service;
}
```

O controller **não cria** o `UsuarioService`.  
Quem cria e injeta é o Spring.

Outro exemplo importante:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

O bean `PasswordEncoder` nasce na camada `config`, e depois é injetado na camada `service`.

## 4. Onde está a Inversão de Controle (IoC)?

A IoC acontece quando o controle da criação e ligação dos objetos sai da sua mão e vai para o framework.

Sem Spring:

```java
UsuarioRepository repo = ...
PasswordEncoder encoder = ...
UsuarioService service = new UsuarioService(repo, encoder);
UsuarioRestController controller = new UsuarioRestController(service);
```

Com Spring, você apenas declara dependências no construtor, e o framework resolve tudo.

## 5. Como testar cada camada individualmente

## 5.1 Testar a camada Controller
A forma mais fácil é subir a aplicação e usar:
- Swagger
- Postman
- Insomnia
- front estático

Endpoints principais:
- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `POST /api/usuarios`
- `GET /api/cursos`
- `POST /api/cursos`
- `POST /api/matriculas`
- `PUT /api/matriculas/{id}/concluir`
- `GET /api/matriculas/usuario/{usuarioId}`

No Swagger, abra:

```text
http://localhost:8080/swagger-ui/index.html
```

Login HTTP Basic:
- usuário: `admin`
- senha: `123456`

## 5.2 Testar a camada Service
A camada service normalmente é testada com **testes unitários** ou **testes de integração**.

Sugestão didática:
- testar `UsuarioService.criar()`;
- testar `MatriculaService.matricular()`;
- testar `MatriculaService.concluir()`;
- verificar se a assinatura recebe créditos quando a nota for >= 7.

O que observar:
- regras de negócio corretas;
- exceções lançadas em casos inválidos;
- senha sendo criptografada;
- mudança de plano após várias conclusões.

## 5.3 Testar a camada Repository
Você pode testar a persistência de duas formas:

### Pelo H2 Console
Acesse:

```text
http://localhost:8080/h2-console
```

Configuração:
- JDBC URL: `jdbc:h2:mem:usuariodb`
- User Name: `sa`
- Password: deixar em branco

Consultas úteis:

```sql
SELECT * FROM USUARIOS;
SELECT * FROM ASSINATURAS;
SELECT * FROM CURSOS;
SELECT * FROM MATRICULAS;
```

Para ver relacionamentos na prática:

```sql
SELECT m.ID, m.USUARIO_ID, m.CURSO_ID, m.STATUS, m.NOTA_FINAL, m.BONUS
FROM MATRICULAS m;
```

```sql
SELECT a.ID, a.USUARIO_ID, a.PLANO, a.CREDITOS_CURSOS, a.CURSOS_CONCLUIDOS_COM_SUCESSO, a.MOEDAS
FROM ASSINATURAS a;
```

### Com testes de integração
Outra opção é criar testes com `@DataJpaTest`.

## 5.4 Testar a camada Domain
A camada de domínio pode ser testada diretamente, sem subir o Spring, com testes unitários puros.

Exemplos bons:
- `new EmailUsuario("email-invalido")` deve lançar exceção;
- `new NomeUsuario("   ")` deve lançar exceção;
- `assinatura.consumirCredito()` sem saldo deve falhar;
- `matricula.concluidoComAproveitamento()` deve retornar `true` quando status = `CONCLUIDO` e nota >= 7.

## 6. Como testar o projeto completo

### Passo 1 — Subir a aplicação
No terminal, na raiz do projeto:

```bash
mvn spring-boot:run
```

### Passo 2 — Criar um usuário
Exemplo de JSON:

```json
{
  "nome": "Andreia",
  "email": "andreia@exemplo.com",
  "senha": "123456"
}
```

### Passo 3 — Criar um curso

```json
{
  "titulo": "Spring Boot Fundamentos",
  "descricao": "Curso introdutorio"
}
```

### Passo 4 — Matricular o usuário

```json
{
  "usuarioId": 1,
  "cursoId": 1,
  "bonus": false
}
```

### Passo 5 — Concluir a matrícula com aproveitamento

```json
{
  "notaFinal": 8.0
}
```

### Passo 6 — Buscar o usuário e verificar créditos
Abra:

```text
GET /api/usuarios/1
```

Espera-se ver créditos adicionados na assinatura.

## 7. Dicas pedagógicas para estudar o código

Leia nesta ordem:
1. `UsuarioApplication`
2. `SecurityConfig`
3. `UsuarioRestController`
4. `UsuarioService`
5. `UsuarioRepository`
6. `Usuario`
7. `EmailUsuario`, `NomeUsuario`, `SenhaCriptografada`
8. `ApiExceptionHandler`
9. repetir o mesmo raciocínio para Curso e Matricula

## 8. Perguntas-guia para revisar cada classe

### Controller
- qual URL ela expõe?
- qual verbo HTTP usa?
- qual DTO recebe?
- qual service chama?

### Service
- qual regra de negócio executa?
- qual repository usa?
- tem `@Transactional`?
- faz conversão para DTO?

### Repository
- qual entidade manipula?
- quais métodos herdou do `JpaRepository`?
- há métodos derivados por nome?

### Domain
- qual conceito do negócio representa?
- quais invariantes protege?
- quais relacionamentos tem?
- quais métodos são realmente do negócio?

## 9. Observação final importante

Nesta versão, os comentários foram adicionados para **estudo didático**.  
Em projetos de produção, normalmente usamos comentários com mais parcimônia, priorizando:
- bons nomes de classes e métodos;
- código limpo;
- documentação externa quando necessário.

Mas, para aprender Spring Boot por camadas, esta abordagem é excelente.
