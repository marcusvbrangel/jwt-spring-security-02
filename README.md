# JWT Spring Security API

Este projeto é um exemplo completo de API RESTful com autenticação JWT, controle de acesso com Spring Security, arquitetura limpa, integração com PostgreSQL, documentação Swagger/OpenAPI e pronto para uso com Docker Compose.

## Como rodar o projeto

### Pré-requisitos
- Java 21
- Docker e Docker Compose
- Maven (ou use o wrapper `./mvnw`)

### Subindo o banco de dados e pgAdmin

```
docker-compose up -d
```

- PostgreSQL: `localhost:5432` (db: `jwt_db`, user: `jwt_user`, senha: `jwt_pass`)
- PgAdmin: `localhost:5050` (login: `admin@admin.com` / senha: `admin`)

### Rodando a aplicação

```
./mvnw spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080

## Documentação Swagger

Acesse: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Usuários e Perfis Iniciais
- Usuário ADMIN: `admin` / senha: `admin`
- Usuário USER: `user` / senha: `user`

## Endpoints principais

### Autenticação
- **POST** `/api/v1/auth/login`  
  Body: `{ "username": "admin", "password": "admin" }`

### Usuários (apenas ADMIN)
- **POST** `/api/v1/auth/users`  
  Body: `{ "username": "novo", "password": "senha", "roles": ["USER"] }`

### Produtos
- **POST** `/api/v1/products` (USER)
- **PUT** `/api/v1/products/{id}` (USER, dono)
- **DELETE** `/api/v1/products/{id}` (USER, dono)
- **GET** `/api/v1/products/me` (USER)
- **GET** `/api/v1/products` (ADMIN)
- **GET** `/api/v1/products/public` (permitAll)
- **GET** `/api/v1/products/deny` (denyAll)

> Para endpoints protegidos, obtenha o token JWT via login e use no header:
> 
> `Authorization: Bearer {token}`

## Testando com Postman

1. Importe o arquivo `artifacts/postman-collection.json` no Postman.
2. Faça login e copie o token JWT para a variável `{{token}}`.
3. Use os endpoints normalmente, preenchendo também `{{productId}}` para update/delete.

## Testes automatizados

Execute:
```
./mvnw test
```

## Estrutura do projeto
- `src/main/java/com.mvbr.jwtspringsecurity02` - Código fonte (API, domínio, infraestrutura, casos de uso)
- `src/test/java/com.mvbr.jwtspringsecurity02` - Testes unitários
- `docker-compose.yml` - Banco de dados e pgAdmin
- `artifacts/postman-collection.json` - Coleção pronta para Postman

## Observações
- Não é utilizado Lombok.
- Validações com Jakarta Bean Validation.
- Exceções personalizadas e tratamento global de erros.
- Logs de segurança e regras de negócio.
- Arquitetura limpa (separação clara de camadas).

---

Dúvidas ou sugestões? Fique à vontade para abrir uma issue ou contribuir!

