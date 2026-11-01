# Pesquisa Online

> Sistema web para criação, gerenciamento e preenchimento de pesquisas online, com API REST em Java/Spring Boot e interface em Thymeleaf, HTML, CSS e JavaScript.

O **Pesquisa Online** permite criar e gerenciar pesquisas, cadastrar perguntas e opções de resposta e registrar as respostas dos participantes. Administradores montam as pesquisas e acompanham os resultados; participantes respondem às pesquisas disponíveis.

O projeto foi desenvolvido com foco na aplicação prática de conceitos de **Programação Orientada a Objetos, APIs REST, arquitetura em camadas, persistência de dados, validação, regras de negócio e integração entre backend e frontend**.

---

## Sobre o projeto

O sistema permite que pesquisas sejam criadas e disponibilizadas para os usuários responderem.

Cada pesquisa pode possuir diversas perguntas, que podem ter diferentes tipos de resposta (aberta, única escolha ou múltipla escolha). Para perguntas de escolha, também é possível cadastrar as opções.

O sistema possui regras de negócio para controlar o recebimento das respostas, como verificar se uma pesquisa está ativa ou se já foi encerrada.

### Principais recursos

* Cadastro e identificação de usuários, com perfis **Admin** e **Participante**
* Criação e gerenciamento de pesquisas
* Cadastro e gerenciamento de perguntas e opções de resposta
* Tela para o participante responder às pesquisas
* Tela de resultados para o administrador
* Registro de respostas
* Controle de pesquisas ativas e encerradas
* Validação dos dados recebidos
* Tratamento global de exceções
* Aplicação de regras de negócio
* Persistência de dados utilizando JPA/Hibernate

---

## Tecnologias

| Tecnologia              | Utilização                           |
| ----------------------- | ------------------------------------ |
| Java                    | Linguagem principal                  |
| Spring Boot             | Desenvolvimento da aplicação         |
| Spring Web              | Construção da API REST               |
| Spring Data JPA         | Persistência de dados                |
| Hibernate               | ORM                                  |
| Bean Validation         | Validação dos dados                  |
| Thymeleaf               | Templates das páginas HTML           |
| HTML, CSS e JavaScript  | Interface e consumo da API (`fetch`) |
| MySQL                   | Banco de dados                       |
| Maven                   | Gerenciamento de dependências        |
| Postman                 | Testes da API                        |
| Git e GitHub            | Controle de versão e hospedagem      |

---

## Arquitetura

O projeto utiliza uma **arquitetura em camadas** no backend, e o frontend é servido pela própria aplicação Spring Boot.

```text
┌─────────────────────────────────────────┐
│  Navegador (páginas Thymeleaf + JS)     │
│  ou Postman                             │
└───────────────────┬─────────────────────┘
                    │ HTTP
                    ▼
┌─────────────────────────────────────────┐
│  PaginaController      Controllers REST │
│  (entrega as páginas)  (JSON / API)     │
└───────────────────┬─────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│               Service                   │
│          Regras de negócio              │
└───────────────────┬─────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│             Repository                  │
│          Acesso aos dados               │
└───────────────────┬─────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│                MySQL                    │
└─────────────────────────────────────────┘
```

### Camadas

**PaginaController**

Entrega as páginas HTML (templates Thymeleaf). As rotas terminam em `.html` (por exemplo `/login.html`) para não colidirem com a API REST, que usa rotas como `/pesquisas`.

**Controller**

Recebe as requisições HTTP e disponibiliza os endpoints da API, retornando JSON.

**Service**

Contém as principais regras de negócio do sistema e coordena as operações realizadas pela aplicação.

**Repository**

Responsável pelo acesso e persistência dos dados utilizando Spring Data JPA.

**Entity**

Representa as entidades utilizadas pelo sistema e seus relacionamentos com o banco de dados.

**DTO**

Responsável pelo transporte de dados entre as diferentes partes da aplicação, evitando expor diretamente as entidades em determinadas operações.

**Exception**

Contém as exceções personalizadas e o tratamento global dos erros da aplicação.

---

## Frontend

O frontend fica dentro do próprio projeto Spring Boot:

* `src/main/resources/templates/`: páginas HTML (Thymeleaf)
* `src/main/resources/static/css/`: estilos
* `src/main/resources/static/js/`: scripts que consomem a API REST

### Páginas

| Página            | Quem acessa      | Função                                          |
| ----------------- | ---------------- | ----------------------------------------------- |
| `/`               | Todos            | Redireciona para o login ou para as pesquisas   |
| `/login.html`     | Todos            | Entrar com o e-mail cadastrado                  |
| `/cadastro.html`  | Todos            | Criar conta (perfil Participante ou Admin)      |
| `/pesquisas.html` | Usuário logado   | Lista as pesquisas; o Admin também gerencia     |
| `/editor.html`    | Admin            | Cria e edita pesquisas, perguntas e opções      |
| `/responder.html` | Usuário logado   | Responde a uma pesquisa                         |
| `/resultados.html`| Admin            | Visualiza as respostas de uma pesquisa          |

### Como o frontend se comunica com a API

Os scripts em `static/js/` usam `fetch` para chamar os endpoints REST da própria aplicação. A camada de acesso à API está em `static/js/api.js`, e a sessão do usuário é guardada no navegador em `static/js/auth.js`.

### Limitações atuais da autenticação

* Ainda **não existe endpoint de login com validação de senha**. O login busca o usuário pelo e-mail (`GET /usuarios/email/{email}`) e, se ele existir, inicia a sessão sem conferir a senha.
* A senha é armazenada **sem criptografia** no banco.
* A sessão fica apenas no `localStorage` do navegador, e a API não bloqueia chamadas por perfil.

Esses pontos estão listados em [Próximos passos](#próximos-passos) e não devem ser usados em produção.

---

## Modelo de domínio

O sistema possui as seguintes entidades principais:

```text
┌──────────────┐
│   Usuario    │
└──────┬───────┘
       │ 1:N
       ▼
┌──────────────┐
│   Resposta   │
└──────┬───────┘
       │ N:1
       ▼
┌──────────────┐        ┌──────────────┐
│   Pergunta   │ N:1    │   Pesquisa   │
│              ├───────▶│              │
└──────┬───────┘        └──────────────┘
       │ 1:N
       ▼
┌──────────────┐
│    Opcao     │
└──────────────┘
```

### Entidades

#### Usuario

Representa os usuários cadastrados no sistema.

```text
id
nome
email (único)
senha
perfil  → Admin | Participante
```

#### Pesquisa

Representa uma pesquisa criada no sistema.

```text
id
titulo
descricao
ativa
dataCriacao
dataEncerramento
```

Uma pesquisa pode possuir várias perguntas.

#### Pergunta

Representa uma pergunta pertencente a uma pesquisa.

```text
id
enunciado
tipoPergunta  → Aberta | Unica_Escolha | Multipla_Escolha
obrigatoria
ordem
pesquisa
```

Uma pergunta pertence a uma pesquisa e pode possuir opções e respostas.

#### Opcao

Representa uma alternativa disponível para perguntas de escolha.

```text
id
descricao
pergunta
```

#### Resposta

Representa uma resposta enviada por um usuário.

```text
id
usuario
pergunta
opcao
respostaTexto
dataResposta
```

---

## Regras de negócio

O sistema possui regras para garantir a consistência das operações.

* Uma pesquisa precisa estar ativa para receber respostas.
* Pesquisas com data de encerramento já vencida não recebem novas respostas.
* A opção escolhida deve pertencer à pergunta respondida.
* Perguntas **abertas** exigem `respostaTexto` e não aceitam opção.
* Perguntas de **escolha** exigem uma opção e não aceitam `respostaTexto`.
* O e-mail do usuário é único (restrição no banco de dados).
* Dados obrigatórios são validados antes de serem persistidos.
* Recursos inexistentes geram `ResourceNotFoundException`.
* Regras de negócio violadas geram `BusinessRuleException`.
* A data da resposta é registrada automaticamente no momento da persistência.

---

## Validação e tratamento de erros

A aplicação possui um `GlobalExceptionHandler` que centraliza o tratamento das exceções e devolve sempre o mesmo formato de resposta.

| Exceção                            | Status | Quando ocorre                                     |
| ---------------------------------- | ------ | ------------------------------------------------- |
| `ResourceNotFoundException`        | 404    | Registro não encontrado                           |
| `NoResourceFoundException`         | 404    | Rota ou arquivo inexistente                       |
| `MethodArgumentNotValidException`  | 400    | Falha na validação dos campos                     |
| `BusinessRuleException`            | 400    | Regra de negócio violada                          |
| `HttpMessageNotReadableException`  | 400    | JSON inválido ou valor de enum desconhecido       |
| `Exception` (genérica)             | 500    | Erro inesperado (o detalhe é registrado no log)   |

### Exemplo de resposta de erro

```json
{
  "status": 400,
  "mensagem": "Erro de validação.",
  "erros": {
    "nome": "O nome é obrigatório.",
    "email": "Informe um email válido."
  }
}
```

> Em caso de erro 500, o motivo real aparece no console da aplicação (linha `ERROR`, seguida de `Caused by:`), pois a resposta HTTP traz apenas uma mensagem genérica.

---

## API

A API não usa prefixo; cada recurso fica na raiz.

### Usuários

| Método   | Endpoint                 | Descrição                |
| -------- | ------------------------ | ------------------------ |
| `POST`   | `/usuarios`              | Cadastrar usuário        |
| `GET`    | `/usuarios`              | Listar usuários          |
| `GET`    | `/usuarios/{id}`         | Buscar usuário por id    |
| `GET`    | `/usuarios/email/{email}`| Buscar usuário por e-mail|
| `PUT`    | `/usuarios/{id}`         | Atualizar usuário        |
| `DELETE` | `/usuarios/{id}`         | Excluir usuário          |

### Pesquisas

| Método   | Endpoint          | Descrição          |
| -------- | ----------------- | ------------------ |
| `POST`   | `/pesquisas`      | Criar pesquisa     |
| `GET`    | `/pesquisas`      | Listar pesquisas   |
| `GET`    | `/pesquisas/{id}` | Buscar pesquisa    |
| `PUT`    | `/pesquisas/{id}` | Atualizar pesquisa |
| `DELETE` | `/pesquisas/{id}` | Excluir pesquisa   |

### Perguntas

| Método   | Endpoint          | Descrição          |
| -------- | ----------------- | ------------------ |
| `POST`   | `/perguntas`      | Criar pergunta     |
| `GET`    | `/perguntas`      | Listar perguntas   |
| `PUT`    | `/perguntas/{id}` | Atualizar pergunta |
| `DELETE` | `/perguntas/{id}` | Excluir pergunta   |

### Opções

| Método   | Endpoint        | Descrição        |
| -------- | --------------- | ---------------- |
| `POST`   | `/opcoes`       | Criar opção      |
| `GET`    | `/opcoes`       | Listar opções    |
| `GET`    | `/opcoes/{id}`  | Buscar opção     |
| `PUT`    | `/opcoes/{id}`  | Atualizar opção  |
| `DELETE` | `/opcoes/{id}`  | Excluir opção    |

### Respostas

| Método   | Endpoint          | Descrição            |
| -------- | ----------------- | -------------------- |
| `POST`   | `/respostas`      | Registrar resposta   |
| `GET`    | `/respostas`      | Listar respostas     |
| `PUT`    | `/respostas/{id}` | Atualizar resposta   |
| `DELETE` | `/respostas/{id}` | Excluir resposta     |

> Os endpoints podem ser modificados conforme a evolução do projeto.

---

## Exemplos de requisição

### Cadastro de usuário

`POST /usuarios`

```json
{
  "nome": "Ana Carolina",
  "email": "ana@email.com",
  "senha": "123456",
  "perfil": "Admin"
}
```

O campo `perfil` aceita `Admin` ou `Participante` (com essa grafia).

### Criação de pesquisa

`POST /pesquisas`

```json
{
  "titulo": "Pesquisa de Satisfação",
  "descricao": "Pesquisa para avaliar a satisfação dos usuários.",
  "ativa": true,
  "dataEncerramento": "2026-12-31T23:59:59"
}
```

### Cadastro de pergunta

`POST /perguntas`

```json
{
  "enunciado": "Como você avalia nosso atendimento?",
  "tipoPergunta": "Unica_Escolha",
  "obrigatoria": true,
  "ordem": 1,
  "pesquisaId": 1
}
```

O campo `tipoPergunta` aceita `Aberta`, `Unica_Escolha` ou `Multipla_Escolha`.

### Cadastro de opção

`POST /opcoes`

```json
{
  "descricao": "Excelente",
  "perguntaId": 1
}
```

### Registro de resposta

`POST /respostas`

Pergunta de escolha:

```json
{
  "usuarioId": 1,
  "perguntaId": 1,
  "opcaoId": 1
}
```

Pergunta aberta:

```json
{
  "usuarioId": 1,
  "perguntaId": 2,
  "respostaTexto": "O atendimento foi rápido."
}
```

---

## Banco de dados

O projeto utiliza **MySQL** como banco de dados e **JPA/Hibernate** para o mapeamento objeto-relacional.

As principais relações são:

```text
Usuario 1 ───────── N Resposta

Pesquisa 1 ──────── N Pergunta

Pergunta 1 ──────── N Opcao

Pergunta 1 ──────── N Resposta
```

O Hibernate cria e atualiza as tabelas durante o desenvolvimento através da configuração:

```properties
spring.jpa.hibernate.ddl-auto=update
```

> Atenção: o `update` **não altera colunas que já existem**. Se você renomear um valor de enum (por exemplo `Administrador` para `Admin`), a coluna `perfil` do MySQL pode continuar com os valores antigos. Nesse caso, ajuste a coluna manualmente:
>
> ```sql
> ALTER TABLE usuario MODIFY perfil VARCHAR(20) NOT NULL;
> ```

---

## Estrutura do projeto

```text
pesquisa_online/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/ana/pesquisa_online/
│       │       ├── controller/      API REST e PaginaController
│       │       ├── dto/
│       │       ├── entity/
│       │       │   └── enums/       PerfilUsuario, TipoPergunta
│       │       ├── exception/
│       │       ├── repository/
│       │       └── service/
│       │
│       └── resources/
│           ├── application.properties
│           ├── templates/           Páginas Thymeleaf (.html)
│           └── static/
│               ├── css/style.css
│               └── js/              api.js, auth.js, ui.js e scripts das páginas
│
├── pom.xml
└── README.md
```

---

## Como executar

### Pré-requisitos

* Java (compatível com o `pom.xml`)
* Maven (ou o `mvnw` incluído no projeto)
* MySQL
* Git
* IntelliJ IDEA ou outra IDE compatível

### 1. Clone o repositório

```bash
git clone https://github.com/AnaCS558/pesquisa_online.git
```

### 2. Entre na pasta

```bash
cd pesquisa_online
```

### 3. Crie o banco de dados

No MySQL, crie o banco:

```sql
CREATE DATABASE pesquisa_online;
```

### 4. Configure a aplicação

Abra:

```text
src/main/resources/application.properties
```

Configure suas credenciais do MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pesquisa_online
spring.datasource.username=root
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Substitua `SUA_SENHA` pela senha do seu banco de dados.

### 5. Execute a aplicação

Pelo Maven:

```bash
mvn spring-boot:run
```

Ou execute a classe `PesquisaOnlineApplication` diretamente pela IDE.

A aplicação será iniciada, por padrão, em:

```text
http://localhost:8080
```

### 6. Acesse o sistema

1. Abra `http://localhost:8080` no navegador. Você será levado à tela de login.
2. Clique em **Criar conta** e cadastre um usuário com perfil **Admin**.
3. Como Admin, crie uma pesquisa, adicione perguntas e opções.
4. Cadastre outro usuário com perfil **Participante** (em outra aba anônima ou depois de sair) para responder à pesquisa.
5. Volte ao Admin e abra a tela de resultados.

---

## Testando a API

Para testar os endpoints, pode ser utilizado o **Postman**.

Exemplo:

```text
POST http://localhost:8080/usuarios
```

No corpo da requisição, selecione:

```text
Body → raw → JSON
```

E envie:

```json
{
  "nome": "Ana Carolina",
  "email": "ana@email.com",
  "senha": "123456",
  "perfil": "Participante"
}
```

---

## Problemas comuns

| Sintoma                                                     | Causa provável                                                                 |
| ----------------------------------------------------------- | ------------------------------------------------------------------------------ |
| `{"status":500,"mensagem":"Ocorreu um erro interno..."}`    | Veja o console da aplicação: a linha `ERROR` e o `Caused by:` mostram a causa. |
| Erro ao criar usuário com `perfil` inválido                 | O valor deve ser exatamente `Admin` ou `Participante`.                         |
| Página abre sem estilo ou sem funcionar                     | Cache do navegador: atualize com `Ctrl+F5`.                                    |
| `404` em `/login` ou `/pesquisas` no navegador              | As páginas usam `.html`: `/login.html`, `/pesquisas.html`.                     |
| Erro de coluna ao salvar usuário depois de mudar um enum    | Ajuste a coluna no MySQL (veja a seção Banco de dados).                        |

---

## Conceitos aplicados

* Programação Orientada a Objetos
* Encapsulamento
* Enumerações
* Relacionamentos entre entidades
* Injeção de dependência
* API REST
* Arquitetura em camadas
* DTOs
* JPA e Hibernate
* Validação de dados
* Exceções personalizadas
* Tratamento global de exceções
* Regras de negócio
* Persistência em banco de dados
* Integração entre frontend (Thymeleaf, HTML, CSS e JavaScript) e backend
* Controle de versão com Git

---

## Próximos passos

* Autenticação real com validação de senha
* Spring Security e autorização por perfil
* Criptografia de senhas
* Mensagem de erro clara para e-mail já cadastrado
* Documentação da API com Swagger/OpenAPI
* Paginação e filtros
* Gráficos e dashboard de resultados
* Testes automatizados
* Deploy da aplicação

---

## Status

**Em desenvolvimento**

O projeto está sendo desenvolvido e aprimorado continuamente, com novas funcionalidades e melhorias sendo adicionadas ao longo do desenvolvimento.

---

## Autora

**Ana Carolina Schneider**