# ADR 001: Use Kafka as the ingestion buffer

## Context
Ingestion must be fast while indexing can slow or restart.
## Decision
Publish accepted events to a retained Kafka log and process them asynchronously.
## Alternatives
Direct OpenSearch writes couple latency/failure; RabbitMQ is strong for work queues; Redis Streams is simpler but less aligned with retained replay and partition learning.
## Trade-offs
Kafka adds operational memory, disk, partitions, and offset semantics.
## Consequences
Bursts become measurable lag, records are replayable, and search is eventually consistent.

