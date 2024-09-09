# Testbackend API


# Instruções para Executar a Aplicação com Docker

Este documento fornece as etapas para clonar o repositório, construir e executar a aplicação junto com o banco de dados MySQL usando Docker Compose.

## Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas no seu ambiente de desenvolvimento:

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## 1. Clonando o Repositório

Para obter o código fonte, você precisará clonar o repositório Git. Use o seguinte comando no terminal:

```bash
git clone https://github.com/vilelacode/testbackend.git
```
Depois de clonar o repositório, navegue até o diretório do projeto:
    
```bash
cd seu-repositorio
```
## 2. Configuração do Banco de Dados

Dentro da pasta resources ( \src\test\resources ) do projeto, existe um arquivo chamado schema.sql que contém as estruturas das tabelas do banco de dados.


A aplicação usa um banco de dados MySQL para persistir os dados. Alguns comportamentos do banco de dados foram configurados no arquivo application.properties da aplicação, porém para facilitar a execução da aplicação com Docker,
a configuração do banco de dados é feita no arquivo docker-compose.yml.



## 3. Construindo e Executando a Aplicação


Para construir e executar a aplicação, garanta que a engine do Docker está rodando em sua máquina, então
abra o terminal na pasta docker da aplicação que você acabou de clonar e use o seguinte comando:

```bash
docker-compose up --build
```
Para parar e remover os contêineres e redes criados pelo Docker Compose, use o seguinte comando:

```bash
docker-compose down
```

Ao executar o comando `docker-compose up --build`, o Docker Compose criará os seguintes contêineres:

java_app: Contêiner para a aplicação Spring Boot.

mysql-1: Contêiner para o banco de dados MySQL.

Após a aplicação ser iniciada, você pode acessar a documentação da API em http://localhost:8080/swagger-ui.html. 

Obs.: Enquanto a aplicação Java está iniciando, o JPA gerencia e cria as tabelas do banco de dados com base nas entidades e relacionamentos definidos. Após o JPA configurar o banco, um script SQL data.sql será executado automaticamente para criar três usuários no banco de dados. As configurações de Hibernate, endereço e inicialização estão no arquivo application.properties da API:

```bash
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data.sql

```
A outra definição importante é  *spring.jpa.defer-datasource-initialization=true* é utilizada para evitar conflitos durante a inicialização do EntityManagerFactory com o script SQL.


## Descrevendo a Aplicação

A aplicação é um sistema de gerenciamento de contas bancárias que permite que os usuários se registrem, façam login e realizem transações bancárias.

A aplicação fornece endpoints RESTful para realizar operações CRUD em tais contas.



## Como fazer login na aplicação

Utilizando uma ferramenta como o Postman ou o cURL, você pode fazer login na aplicação para obter um token JWT que você pode usar para acessar os endpoints protegidos.

Para fazer login na aplicação, você precisa enviar uma solicitação POST para o endpoint http://localhost:8080/auth/login com um corpo de solicitação JSON contendo o nome de usuário e a senha. Aqui está um exemplo de solicitação de login:

```json
{
    "login": "admin",
    "senha": "senha123"
}
```

A resposta da solicitação de login incluirá um token JWT que você pode usar para acessar os endpoints protegidos. Aqui está um exemplo de resposta de login:

```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTY0NzQwNzQwNiwiaWF0IjoxNjQ3MzIyMjA2fQ.1 
}
```
A aplicação foi desenvolvida sob o contexto do usuário logado, ou seja, o sistema irá utilizar os dados 
do usuário logado para realizar as operações de transferência, saque e depósito.

Da mesma forma, ao tentar realizar uma operação, o sistema irá salvar nos logs da operação as informações do usuário logado que realizou a operação.

obs.:Sobre a operação de depósito não haverá verificação de valor em conta, pois na simulação o depósito pode ser feito em dinheiro.

## Endpoints da API

Além do login explicado acima, a API fornece os seguintes endpoints para realizar operações CRUD em contas bancárias:

Endpoint para cadastrar uma conta bancária e usuário:

**-POST /conta/criar**

```json
{
  "numeroConta": 123456,
  "nomeTitular": "Teste Usuário",
  "login": "teste.usuario",
  "senha": "senha123"
}
```
Para realizar um deposito em dinheiro:

**-PUT /conta/depositar**

```json
{
    "numeroConta": 123456,
    "valor": 100.00
}
```


Transferir dinheiro de uma conta para outra:

**- PUT /conta/transferir** 

```json
{
    "numeroConta": 111111,
    "valor": 100.00
}
```

Para realizar um saque em dinheiro:

_Nesse caso a inserção do valor é feita diretamente na URL_

**- PUT /conta/sacar/{valor}**

Para listar as transações da conta do usuário logado:

**- GET /conta/saldo-extrato/consulta**

Outras observações:

**Em todas as operações ocorrem validações de dados, como por exemplo, verificar se a conta de destino existe, se o valor a ser transferido é maior que o saldo da conta, se o valor a ser depositado é maior que zero, se o valor a ser sacado é maior que zero, etc.**

**Além disso também ocorre as validações dos tipos de dados dos inputs.**

**Para valores decimais utilize o ponto (.) como separador de casas decimais.**

# Tecnologias Utilizadas

- **Spring Boot**:
  - `spring-boot-starter-data-jpa`: Integração com JPA para persistência de dados.
  - `spring-boot-starter-web`: Criação de APIs RESTful.
  - `spring-boot-starter-validation`: Validação de dados.

- **Spring Security**:
  - `spring-security-web`: Configuração de segurança na camada web.
  - `spring-security-core`: Autenticação e autorização centralizada.
  - `spring-security-config`: Integração de configurações de segurança.

- **JWT**:
  - `com.auth0:java-jwt`: Geração e validação de tokens JWT (JSON Web Token).

- **Banco de Dados**:
  - `mysql-connector-j`: Driver JDBC para conexão com o banco de dados MySQL.
  - `h2`: Banco de dados em memória para testes.

- **Lombok**:
  - `lombok`: Redução de código boilerplate com anotações como `@Getter`, `@Setter`, `@Builder`, etc.

- **Testes**:
  - `spring-boot-starter-test`: Conjunto de ferramentas para testes em Spring Boot.
  - `junit`: Suporte para testes unitários.
  - `spring-security-test`: Testes de segurança.

- **Documentação**:
  - `springdoc-openapi-starter-webmvc-ui`: Geração automática de documentação da API com OpenAPI/Swagger.


