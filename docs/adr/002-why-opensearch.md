# ADR 002: Use OpenSearch for log retrieval

## Context
Logs need full-text search, filters, time windows, and aggregations.
## Decision
Use OpenSearch with explicit mappings and daily indices.
## Alternatives
PostgreSQL full-text search reduces infrastructure but is less natural for this scale/learning goal; Elasticsearch has similar capabilities with different licensing/distribution considerations.
## Trade-offs
OpenSearch needs substantial memory, shard planning, and eventual refresh.
## Consequences
Search is expressive and fast, while control-plane relational data remains in PostgreSQL.

