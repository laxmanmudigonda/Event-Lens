# EventLens interview notes

These answers describe the implemented repository, including its deliberate local-development limitations.

## Java and Spring

1. **Why Java?** Its mature Kafka/Spring ecosystem, strong typing, predictable concurrency model, profiling tools, and common backend use make it a good placement-project choice. EventLens uses immutable records for wire models and explicit service boundaries.
2. **What is dependency injection?** Objects receive dependencies through constructors instead of creating them. Spring builds the object graph, while tests can pass mocks or fakes.
3. **Interface versus abstract class?** An interface defines a capability and allows unrelated implementations; an abstract class shares state or partial behavior. EventLens avoids interfaces where there is only one meaningful implementation.
4. **Why records?** `LogEvent`, requests, errors, and responses are immutable data carriers with generated accessors/equality and less boilerplate. They are unsuitable for JPA entities that need proxy-friendly constructors and mutable hydration.
5. **How does HashMap work?** It hashes a key into a bucket, then uses equality among collisions; modern Java can treeify collision-heavy buckets. Average lookup is O(1), but mutable keys and poor hashes break assumptions.
6. **Threads versus processes?** Threads share a heap and are cheap but share failure/state; processes isolate memory. EventLens runs services as processes/containers and uses request/Kafka threads inside each JVM.
7. **Where is CompletableFuture justified?** Kafka sends are asynchronous, so ingestion composes the producer future into an HTTP response without waiting on OpenSearch. Batch ingestion joins producer futures after all complete.
8. **Why immutable events?** They are safer across threads, make transformations explicit, and prevent accidental mutation between validation, serialization, retry, and indexing.
9. **What do Java streams add?** They express transformations such as futures-to-results and partition metadata clearly. They are not automatically faster and parallel streams are avoided on request paths.
10. **How are exceptions handled?** Domain exceptions identify unauthorized and rate-limited requests; global advice converts them to stable API errors and never returns stack traces.
11. **Why Spring Boot?** It provides cohesive web, validation, Kafka, JPA, Actuator, configuration, and test integration while leaving delivery choices explicit.
12. **What does DI solve here?** Controllers depend on ingestion/search services, consumers on the indexer, and services on clients. Construction and configuration stay centralized and unit tests remain isolated.
13. **How does configuration work?** YAML defines defaults; environment variables override infrastructure addresses, secrets, limits, retry budgets, and ports in containers.
14. **What is Actuator?** It exposes operational endpoints. EventLens enables health, liveness/readiness probes, metrics, and info rather than inventing a parallel health API.
15. **How does request validation work?** Jakarta annotations constrain fields and nested batch elements. `@Valid`, method validation, batch size caps, and global exception mapping produce a 400 response.
16. **Why constructor injection?** Required dependencies are visible, fields can be final, incomplete objects cannot be created, and tests can call constructors directly.
17. **Why Flyway plus `ddl-auto=validate`?** Migrations are versioned and reviewable. Hibernate verifies entities match the schema but does not silently mutate production data structures.

## Kafka

18. **Why Kafka?** It durably decouples bursty ingestion from indexing, supports replay, partitions ordered streams, and lets consumer groups scale processing.
19. **Kafka versus Redis?** Redis is excellent for cache, counters, and lighter streams, but Kafka's durable partition log, retention, replay, and group-offset model better fit this learning goal.
20. **Kafka versus RabbitMQ?** RabbitMQ emphasizes flexible routing and queue delivery; Kafka emphasizes retained append-only logs, high-throughput partitioning, and replay. EventLens benefits from replay and lag visibility.
21. **What is a broker?** A Kafka server storing partition replicas and serving produce/fetch requests. Compose uses one broker; production would use multiple brokers and replication.
22. **What is a partition?** An ordered append-only shard of a topic. It is both the ordering boundary and unit of consumer parallelism.
23. **Why six partitions?** Enough to demonstrate distribution across one to three processors while remaining lightweight locally. The choice caps active group concurrency at six.
24. **What is a consumer group?** Consumers with the same group ID cooperatively own topic partitions. `eventlens-indexers` ensures one active indexer per partition.
25. **What is an offset?** A monotonically increasing record position within one partition. Consumers commit the next position they have durably handled.
26. **How does ordering work?** Kafka orders records inside one partition only. Keying by service keeps one service's events together; there is no global order across services.
27. **What happens if a consumer crashes?** Its session expires, Kafka rebalances its partitions, and another member resumes at committed offsets. Work after the last commit repeats.
28. **What is rebalancing?** Group coordination pauses or transitions assignment when members/topics change. Partitions move among consumers, so shutdown and processing duration influence disruption.
29. **What is consumer lag?** The difference between each partition's end offset and the group's committed offset. EventLens exposes those values through `/api/v1/system/kafka`.
30. **At-most-once, at-least-once, exactly-once?** At-most-once can lose but not repeat; at-least-once can repeat but avoids loss; exactly-once requires coordinated transactional boundaries. EventLens chooses at-least-once plus an idempotent sink.
31. **Why not auto commit?** A timer could commit an event before OpenSearch accepts it, losing that event after a crash. EventLens acknowledges only after indexing or DLT publication.
32. **Why do extra consumers stop helping?** A partition can be assigned to only one member of a group. With six partitions, a seventh consumer is idle unless partition count grows.
33. **What is retention?** Kafka keeps records for a configured time regardless of consumption. Raw logs retain seven days locally and DLT records thirty, enabling replay and diagnosis.
34. **Can Kafka replay?** Yes. Use a new consumer group or reset committed offsets, then consume the retained log again. Idempotent indexing makes replay safer.
35. **What producer guarantees are configured?** `acks=all`, retries, and producer idempotence. In a one-broker demo, `all` still lacks real replica durability; production requires replication factor three and appropriate minimum ISR.
36. **What is backpressure here?** When OpenSearch is slower than producers, events wait durably in Kafka and lag rises. Alerting on lag prevents the buffer from becoming invisible.
37. **How would schema evolution work?** JSON currently prioritizes readability. Compatible additive changes are manageable; larger teams should introduce Avro/Protobuf plus Schema Registry compatibility rules.

## Search

38. **Why OpenSearch?** It provides inverted indexes, filters, time ranges, sorting, and aggregations designed for log exploration, and has a straightforward local open-source distribution.
39. **What is an inverted index?** It maps analyzed terms to documents containing them, so full-text lookup avoids scanning every message.
40. **`text` versus `keyword`?** `message` is `text` and analyzed for words. Service, level, trace ID, project ID, and environment are `keyword` for exact matching and aggregation.
41. **What is an analyzer?** A tokenizer plus filters applied at indexing/search. EventLens uses the standard analyzer for message text; changing analyzers requires deliberate reindexing.
42. **Why explicit mappings?** They prevent accidental type changes and field explosion. The template is strict and arbitrary metadata uses `flat_object`.
43. **Why is deep pagination expensive?** Each shard collects enough top hits to cover the skipped window, and the coordinator merges them. Memory/CPU grows with `from + size`; EventLens caps it at 10,000.
44. **Why time-based indices?** Daily indices bound shard scope and allow old data to be deleted or transitioned by whole index rather than expensive row-like deletion.
45. **What are aggregations?** Distributed computations over indexed values. EventLens uses terms for levels/services and date histograms with an error sub-aggregation for timelines.
46. **How is query injection prevented?** Clients send typed parameters, not raw DSL. The server constructs a fixed query shape and validates size, interval, enum, timestamps, and sort direction.
47. **How are duplicates handled?** Indexing uses HTTP PUT with `eventId` as `_id`. Reprocessing replaces the same document instead of appending another.

## Distributed systems and design

48. **What is eventual consistency?** Kafka acceptance and OpenSearch indexing are separate moments. A successful POST can precede search visibility by polling, processing, and refresh delay.
49. **What is idempotency?** Repeating an operation has the same final effect. A stable event ID gives repeated indexing one final document.
50. **What happens during indexing failure?** The consumer attempts three times with increasing backoff. It then sends the original event plus error/topic/partition/offset/time/retry data to the DLT and commits only after that send succeeds.
51. **Why a dead-letter topic?** Poison or persistently failing records must not block a partition forever or disappear. A retained diagnostic stream supports inspection, repair, and replay.
52. **Where are the bottlenecks?** OpenSearch write capacity, hot keys/partitions, broker disk/network, synchronous per-document indexing, and local CPU/RAM. Lag and indexing rejection reveal pressure.
53. **What happens at one million logs/minute?** Batch producer records, compress, partition by a key with acceptable spread, add brokers/replicas, bulk-index with tuned processors, roll indices by size/time, and benchmark shard count and refresh interval.
54. **How would ingestion scale?** Run stateless replicas behind a load balancer. Move the in-memory quota to Redis or an API gateway and keep Kafka producer settings identical.
55. **How would consumers scale?** Add group members up to partition count, then increase partitions if ordering/key distribution permits. Bulk writes usually matter before unlimited replicas.
56. **How would OpenSearch scale?** Use multiple data nodes, replicas, shard sizing/rollover, hot-warm tiers, bulk indexing, snapshot lifecycle, and disk/heap safeguards.
57. **How is data loss prevented?** Kafka producer acknowledgment, no auto commit, commit-after-sink-or-DLT, retention, and idempotent replay. Local replication factor one remains a documented single-machine limitation.
58. **How would multi-region ingestion work?** Prefer regional Kafka/ingestion stacks, replicate topics asynchronously, include region in identity/metadata, and define conflict/ordering scope. A single cross-region broker path adds latency and failure coupling.
59. **How would storage cost fall?** Shorten hot retention, roll indices, reduce unnecessary fields, compress source, move old indices to warm/cold object-backed storage, snapshot, then delete by policy.
60. **What if Kafka is unavailable?** The send future fails and the API does not issue 202. Clients retry with the same event ID; a production edge could spool locally with strict bounds.
61. **What if PostgreSQL is unavailable?** New authentication checks and alert metadata fail, so ingestion readiness is down. Already accepted Kafka records can still be processed because processors do not require PostgreSQL.
62. **Why separate PostgreSQL from OpenSearch responsibilities?** Relational constraints and migrations suit control-plane entities. Search-optimized denormalized documents and retention suit logs. Duplicating every raw log in SQL would add cost without helping the main access path.
63. **How does rate limiting work and what is its trade-off?** A per-project, per-minute in-memory counter protects a single local replica. It is simple but not globally correct when replicas scale; Redis/API-gateway quotas are documented next steps.
64. **How do liveness and readiness differ?** Liveness asks whether a process should be restarted. Readiness asks whether it should receive traffic and should include required dependencies. Compose and Kubernetes probe Actuator endpoints.
65. **What would “exactly once” require?** Kafka transactions can coordinate consume-and-produce within Kafka, but OpenSearch is an external sink. End-to-end effect still needs idempotent writes or a transactional outbox/connector with defined guarantees.
66. **Why not start with Kubernetes?** It multiplies operational variables before application semantics are proven. EventLens makes Compose the verified baseline, then deploys stateless application tiers to Kubernetes.
67. **What would you improve first?** Bulk OpenSearch indexing, integration tests using Testcontainers, distributed rate limiting, DLT durable UI storage, alert cooldowns, and OpenSearch lifecycle policy—chosen after measuring the current baseline.

