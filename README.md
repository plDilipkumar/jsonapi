# Recipe JSON API

A Spring Boot and MySQL backend codebase for recipe-oriented data and JSON API workflows.

## Stack

- Java 17
- Spring Boot 3
- Spring Web and Spring Data JPA
- MySQL
- Maven

## Configuration

Provide the database settings through environment variables rather than committing local credentials:

- `SPRING_DATASOURCE_URL` — JDBC URL for the recipes database
- `SPRING_DATASOURCE_USERNAME` — database user
- `SPRING_DATASOURCE_PASSWORD` — database password

For local development, the defaults target a MySQL instance on localhost. In hosted environments, set all three variables explicitly and use TLS when required by the database provider.

## Run locally

```bash
mvn spring-boot:run
```

Hibernate updates entity tables in the configured schema. Create the database first and use a least-privilege database account. The repository does not currently advertise a verified public deployment.
