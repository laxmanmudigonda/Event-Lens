# Architecture

EventLens separates the fast acceptance path from the slower search-indexing path. The ingestion service validates identity and shape, converts requests into immutable `LogEvent` records, and waits only for Kafka producer completion. Processing replicas share a consumer group. The query service is deliberately read-oriented and owns search, analytics, alert evaluation, lag reporting, and DLT administration.

```mermaid
sequenceDiagram
  participant C as Client
  participant I as Ingestion
  participant K as Kafka
  participant P as Processor
  participant O as OpenSearch
  participant Q as Query
  C->>I: POST /api/v1/logs
  I->>I: authenticate, validate, rate-limit
  I->>K: publish(key=service, acks=all)
  K-->>I: acknowledged
  I-->>C: 202 + eventId
  K->>P: poll partition
  P->>O: PUT document(eventId)
  O-->>P: indexed
  P->>K: commit offset
  C->>Q: GET /api/v1/search
  Q->>O: controlled bool query
  O-->>Q: hits
```

At-least-once delivery is chosen because acknowledging before indexing risks loss. The unavoidable duplicate window is closed at the sink with deterministic document IDs. Backpressure accumulates as Kafka lag instead of tying up HTTP request threads.

