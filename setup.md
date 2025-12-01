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

## Swagger UI
Once the app is running, open Swagger UI in your browser:
```
http://localhost:8080/swagger-ui.html
```

The OpenAPI JSON is available at:
```
http://localhost:8080/v3/api-docs
```

## Module Notes
- `eaglebank-app` depends on module peers (`user-api`, `user-impl`, `account`, `security`, `transaction`) and hosts the Boot entrypoint.
- No business logic is implemented yet; endpoints are present as stubs for scaffolding. Use Swagger to inspect available routes.
