# Pesquisa Online

> API REST para criação, gerenciamento e preenchimento de pesquisas online, desenvolvida com Java e Spring Boot.

O **Pesquisa Online** é um sistema desenvolvido para permitir a criação e o gerenciamento de pesquisas, possibilitando o cadastro de perguntas, opções de resposta e o registro das respostas dos usuários.

O projeto foi desenvolvido com foco na aplicação prática de conceitos de **Programação Orientada a Objetos, APIs REST, arquitetura em camadas, persistência de dados, validação e regras de negócio**.

---

## Sobre o projeto

O sistema permite que pesquisas sejam criadas e disponibilizadas para os usuários responderem.

Cada pesquisa pode possuir diversas perguntas, que podem ter diferentes tipos de resposta. Para perguntas de múltipla escolha, também é possível cadastrar diferentes opções.

O sistema possui regras de negócio para controlar o recebimento das respostas, como verificar se uma pesquisa está ativa ou se já foi encerrada.

### Principais recursos

* Gerenciamento de usuários
* Criação e gerenciamento de pesquisas
* Cadastro e gerenciamento de perguntas
* Cadastro de opções de resposta
* Registro de respostas
* Controle de pesquisas ativas e encerradas
* Validação dos dados recebidos
* Tratamento global de exceções
* Aplicação de regras de negócio
* Persistência de dados utilizando JPA/Hibernate

---

## Tecnologias

| Tecnologia      | Utilização                    |
| --------------- | ----------------------------- |
| Java            | Linguagem principal           |
| Spring Boot     | Desenvolvimento da aplicação  |
| Spring Web      | Construção da API REST        |
| Spring Data JPA | Persistência de dados         |
| Hibernate       | ORM                           |
| Bean Validation | Validação dos dados           |
| MySQL           | Banco de dados                |
| Maven           | Gerenciamento de dependências |
| Postman         | Testes da API                 |
| Git             | Controle de versão            |
| GitHub          | Hospedagem do código          |

---

## Arquitetura

O projeto utiliza uma **arquitetura em camadas**, separando as responsabilidades da aplicação.

```text
┌─────────────────────────────┐
│          Client             │
│       Postman / Frontend    │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│         Controller          │
│       API / Endpoints       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│           Service           │
│        Regras de negócio    │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│         Repository          │
│      Acesso aos dados       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│          MySQL              │
│          Database           │
└─────────────────────────────┘
```

### Camadas

**Controller**

Responsável por receber as requisições HTTP e disponibilizar os endpoints da API.

**Service**

Contém as principais regras de negócio do sistema e coordena as operações realizadas pela aplicação.

**Repository**

Responsável pelo acesso e persistência dos dados utilizando Spring Data JPA.

**Entity**

Representa as entidades utilizadas pelo sistema e seus relacionamentos com o banco de dados.

**DTO**

Responsável pelo transporte de dados entre as diferentes partes da aplicação, evitando a necessidade de expor diretamente as entidades em determinadas operações.

**Exception**

Contém as exceções personalizadas e o tratamento global dos erros da aplicação.

---

## Modelo de domínio

O sistema possui as seguintes entidades principais:

```text
┌──────────────┐
│   Usuario    │
└──────┬───────┘
       │
       │ 1:N
       ▼
┌──────────────┐
│   Resposta   │
└──────┬───────┘
       │
       │ N:1
       ▼
┌──────────────┐
│   Pergunta   │
└──────┬───────┘
       │
       │ N:1
       ▼
┌──────────────┐
│   Pesquisa   │
└──────────────┘

┌──────────────┐
│   Pergunta   │
└──────┬───────┘
       │
       │ 1:N
       ▼
┌──────────────┐
│    Opcao     │
└──────────────┘
```

### Entidades

#### Usuario

Representa os usuários cadastrados no sistema.

Principais atributos:

```text
id
nome
email
senha
perfil
```

#### Pesquisa

Representa uma pesquisa criada no sistema.

Principais atributos:

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

Principais atributos:

```text
id
enunciado
tipoPergunta
obrigatoria
ordem
```

Uma pergunta pertence a uma pesquisa e pode possuir opções e respostas.

#### Opcao

Representa uma alternativa disponível para perguntas que possuem opções de resposta.

#### Resposta

Representa uma resposta enviada por um usuário.

Principais atributos:

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

Entre elas:

* Uma pesquisa precisa estar ativa para receber respostas.
* Pesquisas encerradas não podem receber novas respostas.
* Dados obrigatórios são validados antes de serem persistidos.
* O e-mail do usuário deve ser único.
* Recursos inexistentes geram uma exceção específica.
* Regras inválidas de negócio geram `BusinessRuleException`.
* Erros de validação são tratados de forma global.
* A data da resposta é registrada automaticamente no momento da persistência.

---

## Validação e tratamento de erros

A aplicação possui um `GlobalExceptionHandler` responsável por centralizar o tratamento das principais exceções.

São utilizadas exceções personalizadas como:

```text
ResourceNotFoundException
BusinessRuleException
```

Também são tratados erros de validação através do:

```text
MethodArgumentNotValidException
```

### Exemplo de resposta de erro

```json
{
  "status": 400,
  "mensagem": "Erro de validação",
  "erros": {
    "nome": "O nome é obrigatório",
    "email": "O email deve ser válido"
  }
}
```

Essa abordagem permite que a API mantenha um padrão consistente nas respostas de erro.

---

## API

A API disponibiliza endpoints para os principais recursos do sistema.

### Usuários

| Método   | Endpoint         | Descrição         |
| -------- | ---------------- | ----------------- |
| `POST`   | `/usuarios`      | Cadastrar usuário |
| `GET`    | `/usuarios`      | Listar usuários   |
| `GET`    | `/usuarios/{id}` | Buscar usuário    |
| `PUT`    | `/usuarios/{id}` | Atualizar usuário |
| `DELETE` | `/usuarios/{id}` | Excluir usuário   |

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
| `GET`    | `/perguntas/{id}` | Buscar pergunta    |
| `PUT`    | `/perguntas/{id}` | Atualizar pergunta |
| `DELETE` | `/perguntas/{id}` | Excluir pergunta   |

### Respostas

| Método | Endpoint          | Descrição          |
| ------ | ----------------- | ------------------ |
| `POST` | `/respostas`      | Registrar resposta |
| `GET`  | `/respostas`      | Listar respostas   |
| `GET`  | `/respostas/{id}` | Buscar resposta    |

> Os endpoints podem ser modificados conforme a evolução do projeto.

---

## Exemplo de requisição

### Cadastro de usuário

`POST /usuarios`

```json
{
  "nome": "Ana Carolina",
  "email": "ana@email.com",
  "senha": "123456",
  "perfil": "USUARIO"
}
```

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
  "tipoPergunta": "MULTIPLA_ESCOLHA",
  "obrigatoria": true,
  "ordem": 1,
  "pesquisaId": 1
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

O Hibernate pode ser utilizado para atualizar a estrutura das tabelas durante o desenvolvimento através da configuração:

```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## Estrutura do projeto

```text
pesquisa_online/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── ana/
│       │           └── pesquisa_online/
│       │               │
│       │               ├── controller/
│       │               ├── dto/
│       │               ├── entity/
│       │               ├── enums/
│       │               ├── exception/
│       │               ├── repository/
│       │               └── service/
│       │
│       └── resources/
│           └── application.properties
│
├── pom.xml
└── README.md
```

---

## Como executar

### Pré-requisitos

Certifique-se de possuir instalado:

* Java
* Maven
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

Ou execute a classe principal do Spring Boot diretamente pela IDE.

A aplicação será iniciada, por padrão, em:

```text
http://localhost:8080
```

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
  "perfil": "USUARIO"
}
```

---

## Conceitos aplicados

Durante o desenvolvimento foram aplicados conceitos importantes de desenvolvimento de software:

* Programação Orientada a Objetos
* Encapsulamento
* Enumerações
* Relacionamentos entre entidades
* Injeção de dependência
* API REST
* Arquitetura em camadas
* DTOs
* JPA
* Hibernate
* Validação de dados
* Exceções personalizadas
* Tratamento global de exceções
* Regras de negócio
* Persistência em banco de dados
* Controle de versão com Git

---

## Próximos passos

Algumas funcionalidades que podem ser implementadas futuramente:

* Autenticação e autorização de usuários
* Spring Security
* Criptografia de senhas
* Interface web para criação e preenchimento das pesquisas
* Documentação da API com Swagger/OpenAPI
* Paginação e filtros
* Relatórios de respostas
* Dashboard de resultados
* Testes automatizados
* Deploy da aplicação

---

## Status

**Em desenvolvimento**

O projeto está sendo desenvolvido e aprimorado continuamente, com novas funcionalidades e melhorias sendo adicionadas ao longo do desenvolvimento.

---

## Autora

**Ana Carolina Schneider**