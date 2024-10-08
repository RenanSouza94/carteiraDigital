# Carteira Digital

## Descrição
Uma aplicação de carteira digital que permite operações de adição, retirada, compras e estornos.
![Diagrama-carteira-digital](/Diagrama-carteira-digital.png)
## Tecnologias
- Java 17
- Spring Boot
- H2 Database
- Docker
- Swagger
- RabbitMQ

## Execução
1. Clone o repositório.
2. Compile o projeto com Maven: `mvn clean install`
3. Acesse com o terminal a pasta desse projeto e execute o comando: `docker-compose up --build`
4. Acesse a API em `http://localhost:8080/api/transacao` e a documentação do Swagger em `http://localhost:8080/swagger-ui.html`.

## Estrutura do Projeto
- **src**: Código-fonte da aplicação
- **Dockerfile**: Configuração do Docker
- **schema.sql**: Scripts SQL para criação de tabelas

## Testes
Os testes unitários estão localizados na pasta `src/test/java`.

