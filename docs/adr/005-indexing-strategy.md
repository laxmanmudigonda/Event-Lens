# ADR 005: Daily strict-mapped indices

## Context
Log volume is time-oriented and custom metadata can explode mappings.
## Decision
Create `eventlens-logs-YYYY-MM-DD` through a strict template; analyze message, keep filter dimensions as keywords, and map metadata as `flat_object`.
## Alternatives
A single index grows forever; dynamic object mapping is flexible but unsafe; data streams would be a strong production evolution.
## Trade-offs
Cross-day queries touch multiple indices and strict mappings require planned schema changes.
## Consequences
Retention is index-granular and mapping cardinality stays controlled.

