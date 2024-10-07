# Carteira Digital

## Descrição
Uma aplicação de carteira digital que permite operações de adição, retirada, compras e estornos.

## Tecnologias
- Java 17
- Spring Boot
- H2 Database
- Docker
- Swagger

## Execução
1. Clone o repositório.
2. Compile o projeto com Maven: `mvn clean install`
3. Execute o aplicativo: `docker build -t carteiradigital-app . && docker run -p 8080:8080 carteiradigital-app`
4. Acesse a API em `http://localhost:8080/api/transacao` e a documentação do Swagger em `http://localhost:8080/swagger-ui.html`.

## Estrutura do Projeto
- **src**: Código-fonte da aplicação
- **Dockerfile**: Configuração do Docker
- **migrations**: Scripts SQL para criação de tabelas

## Testes
Os testes unitários e integrados estão localizados na pasta `src/test/java`.

