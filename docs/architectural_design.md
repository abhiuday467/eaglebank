# Modular Monolith Architecture Blueprint

## 1. Architectural Summary
* **Project Structure:** Multi-module build. Strict modularity where API modules (`user-api`) are physical **siblings** to implementation modules (`user-impl`).
* **Dependency Rule:** Consumers (Account, Security) depend **only** on the API module of Providers. They cannot see or compile against implementation classes.
* **Database Strategy:** Single physical database, separate schemas (`schema_user`, `schema_account`), single shared DataSource.
* **Migration:** **Flyway** scripts stored locally in each module to allow future extraction.
* **Transactions:** ACID transactions are preserved across module boundaries using Spring’s standard `@Transactional` (Shared Transaction Manager).
* **Data Integrity:** **No database Foreign Keys** between schemas. Integrity is enforced via application logic ("Check-then-Act") and SQL Row Locking (`FOR SHARE`).
* **Testing:**
    * **Unit Tests:** For Service layer logic (using Mockito).
    * **Integration Tests:** For Repository layer (using **Testcontainers**) to verify SQL locking and schema constraints without Foreign Keys.
* **Security:** `eaglebank-security` module handles JWTs; passwords stored in `schema_user` but validated via User API.

---

## 2. Recommended Project Structure

```text
root-project/
├── pom.xml                      (Aggregator)
│
├── eaglebank-user-api/          (Compiled JAR: Interfaces & DTOs)
│
├── eaglebank-user-impl/         (Runtime: User Logic, Password Hashing, JDBC)
│   ├── src/main/java/com/eaglebank/user/...
│   ├── src/main/resources/db/migration/user/...
│   └── src/test/java/com/eaglebank/user/...
│
├── eaglebank-security/          (Runtime: Depends on user-api; implements auth/JWT)
│   ├── src/main/java/com/eaglebank/security/...
│   └── src/test/java/com/eaglebank/security/...
│
├── eaglebank-account/           (Placeholder module; depends on user-api, no main sources yet)
│   └── src/test/java/com/eaglebank/account/...
│
├── eaglebank-transaction/       (Placeholder module; no sources yet)
│
└── eaglebank-app/               (Main Boot Application)
    └── src/main/resources/application.properties
```
