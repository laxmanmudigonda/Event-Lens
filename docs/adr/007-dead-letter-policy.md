# ADR 007: Bounded retry then dead-letter

## Context
Infinite retries block a partition, while dropping failures loses evidence.
## Decision
Attempt indexing three times with increasing backoff, then publish a diagnostic envelope to a 30-day DLT. Commit only after index or DLT success.
## Alternatives
Infinite retry, immediate DLT, or a retry-topic ladder. Retry topics scale better for long delays but add complexity.
## Trade-offs
Blocking retries temporarily reduce that consumer's throughput and DLT inspection is a bounded in-memory view.
## Consequences
Poison records cannot stall the stream forever and are inspectable/replayable.
