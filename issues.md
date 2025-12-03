Issue 1: Flyway migration version collision across modules
- Context: With `spring.flyway.locations=classpath:db/migration/user,classpath:db/migration/account`, Flyway treats all scripts as one stream. Having `V1__create_user_schema.sql` and `V1__create_account_schema.sql` caused startup failure (“Found more than one migration with version 1”).
- Resolution applied: Renamed account migration to `V2__create_account_schema.sql` so versions are unique globally.
- Pros (global versioning): Simple single Flyway instance; works with current Boot auto-config; no extra wiring.
- Cons (global versioning): Teams must coordinate version numbers across modules; harder to keep module-local version history.
- Alternative: Configure separate Flyway beans per schema with isolated `locations` and `schemas`, allowing independent version sequences but requiring custom config and migration ordering awareness.

Issue 2: No structured logging in auth/user flows
- Context: Security and user services/controllers currently emit no logs for auth attempts, failures (e.g., bad credentials), or duplicate email conflicts. This makes troubleshooting and auditability difficult.
- Impact: Harder to trace issues in production and to support security monitoring; no visibility into suspicious login activity.
- Suggested fix: Add SLF4J logging with appropriate levels (INFO for successful login/create, WARN for invalid credentials/duplicate email, ERROR for unexpected failures) in `AuthService/AuthController` and `UserServiceImpl`, ensuring no sensitive data (passwords) is logged.

Issue 3: Temporary permissive CORS to enable Docker-hosted OpenAPI UI (Affected file is CorsConfig.java)
- Context: Added short-term CORS allowances to unblock running Swagger UI via Docker mounting `openapi.yaml`.
- Impact: `CorsConfig` currently permits origins `http://localhost:8082` and `http://localhost:3000` for all paths; this is broader than ideal for production.
- Suggested fix: Replace with environment-driven, minimal-origin CORS config aligned to expected frontends; consider moving to Spring Security CORS to consolidate policy.

Issue 4: Dependencies tied to main app POM for future microservice splits
- Context: Some dependencies are declared only in the main `eaglebank-app` POM. If modules are split into separate microservices later, each new service will need its own POM with the required dependencies copied over.
- Impact: Extracted services will fail to build/run if their needed dependencies remain only in the monolith POM.
- Suggested fix: Document module-specific dependencies and ensure any new microservice POM explicitly includes its runtime/test dependencies rather than relying on `eaglebank-app`.
