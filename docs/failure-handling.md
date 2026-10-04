# Failure handling

| Failure | Observable behavior | Recovery |
|---|---|---|
| Consumer exits before commit | Partition is reassigned; event may repeat | Idempotent `eventId` overwrites duplicate |
| OpenSearch is unavailable | Three increasing-backoff attempts, then DLT | Inspect and replay after recovery |
| DLT publication also fails | Offset remains uncommitted | Kafka redelivers later |
| Kafka is unavailable during ingest | Producer future fails; request is not reported accepted | Client retries with stable `eventId` |
| Query service is unavailable | Ingestion and indexing continue | Restart stateless query replicas |
| PostgreSQL is unavailable | Auth/admin/alerts fail readiness | Restore DB; Kafka data path remains durable |

The local failure demo stops OpenSearch, submits a record, waits for the retry budget, restarts OpenSearch, inspects the DLT, and permits replay. No path silently acknowledges a record before it is indexed or placed on the DLT.

