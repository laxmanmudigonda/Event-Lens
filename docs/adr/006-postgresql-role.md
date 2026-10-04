# ADR 006: PostgreSQL is control-plane storage

## Context
Projects, credentials, and rules need constraints and transactions; logs need search.
## Decision
Store projects, hashed keys, alert rules, and alert history in PostgreSQL. Do not duplicate raw logs there.
## Alternatives
OpenSearch for everything weakens relational integrity; PostgreSQL for everything underuses search-native capabilities.
## Trade-offs
Two datastores must be operated.
## Consequences
Each store has a clear responsibility and Flyway owns relational schema evolution.

