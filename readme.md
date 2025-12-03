# Eaglebank Setup & Usage

## Prerequisites
- Java 17 installed and on your `PATH`
- Maven 3.9+ installed

## Build
Run from the repo root to build all modules:
```bash
mvn clean install
```

## Run
Start the main Spring Boot application (aggregator root):
```bash
mvn -pl eaglebank-app spring-boot:run
```

The app starts on port 8080 by default. Adjust via `server.port` if needed.

## Swagger UI (local app)
Once the app is running, open Swagger UI in your browser:
```
http://localhost:8080/swagger-ui/index.html
```

## Swagger UI (static OpenAPI via Docker)
If you want to view the provided `openapi.yaml` without relying on the in-app UI, run Swagger UI in Docker from the repo root:
```bash
docker run -p 8082:8080 \
  -e SWAGGER_JSON=/app/openapi.yaml \
  -v "$(pwd)/eaglebank-app/src/main/resources/openapi.yaml:/app/openapi.yaml" \
  swaggerapi/swagger-ui
```
Then browse to `http://localhost:8082` to view the docs.

## Local Database (Docker)
Start Postgres via Docker Compose from the repo root:
```bash
docker compose up -d
```
This brings up `postgres:15-alpine` exposed on `localhost:5432` with credentials `eagleuser` / `eaglepass` and database `eaglebank`. Data persists in the `postgres_data` volume. Stop with `docker compose down` when finished.

## Module Notes
- `eaglebank-app` depends on module peers (`user-api`, `user-impl`, `account`, `security`, `transaction`) and hosts the Boot entrypoint.
- No business logic is implemented yet; endpoints are present as stubs for scaffolding. Use Swagger to inspect available routes.
