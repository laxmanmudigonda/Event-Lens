# 4–6 minute demo script

**0:00–0:35 — Problem.** “EventLens centralizes structured logs from independent services. Unlike a CRUD demo, the interesting part is the asynchronous boundary and what happens during duplicates and failures.”

**0:35–1:10 — Architecture.** Show the README diagram. Explain that ingestion authenticates and validates, Kafka buffers six ordered partitions, a consumer group indexes OpenSearch, PostgreSQL holds only control-plane state, and the query service drives the dashboard.

**1:10–1:45 — Ingestion.** Submit an ERROR event and point out the 202 plus generated event ID. Explain that 202 means Kafka accepted it, not that it is already searchable—this is eventual consistency.

**1:45–2:20 — Kafka.** Show the topic and consumer-group describe output. Explain the service record key, partition-local ordering, committed offsets, lag, and why seven consumers cannot actively process six partitions.

**2:20–3:05 — Search and analytics.** Filter payment-service ERROR logs, perform a message search, then open Trace View. Show service/level buckets and the minute timeline; distinguish analyzed `text` from exact `keyword` fields.

**3:05–3:45 — Reliability.** Run the duplicate test and explain deterministic document IDs. Stop one processor and show reassignment. Stop OpenSearch, submit an event, show bounded retries and its diagnostic DLT envelope, then restore and replay it.

**3:45–4:25 — Scaling.** Scale processors to three and show partition redistribution. Show per-partition lag. Explain that Kafka absorbs bursts and exposes backpressure as lag.

**4:25–5:00 — Engineering depth.** Briefly show Flyway, tests, health probes, CI, Kubernetes resource limits, and actual benchmark methodology. Close with the main lessons: delivery semantics require end-to-end design, ordering has a scope, and idempotency makes redelivery safe.

