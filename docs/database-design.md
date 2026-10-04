# Database design

PostgreSQL stores control-plane state, not logs. Flyway migration `V1` creates projects, hashed API keys, alert rules, and alert history. Both JPA applications use `ddl-auto=validate`; only the ingestion service runs migrations so schema ownership is unambiguous.

API keys are generated from secure random bytes. Only a short display prefix and SHA-256 hash are stored, and the raw key is returned once. For a public multi-user system, use a keyed HMAC/pepper, rotation, audit fields, and constant-time comparison. Log documents remain in OpenSearch, whose data model and retention behavior fit search workloads better than relational rows.

