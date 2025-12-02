Issue: Flyway migration version collision across modules
- Context: With `spring.flyway.locations=classpath:db/migration/user,classpath:db/migration/account`, Flyway treats all scripts as one stream. Having `V1__create_user_schema.sql` and `V1__create_account_schema.sql` caused startup failure (“Found more than one migration with version 1”).
- Resolution applied: Renamed account migration to `V2__create_account_schema.sql` so versions are unique globally.
- Pros (global versioning): Simple single Flyway instance; works with current Boot auto-config; no extra wiring.
- Cons (global versioning): Teams must coordinate version numbers across modules; harder to keep module-local version history.
- Alternative: Configure separate Flyway beans per schema with isolated `locations` and `schemas`, allowing independent version sequences but requiring custom config and migration ordering awareness.
