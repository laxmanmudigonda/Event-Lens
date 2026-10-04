# Scaling

Ingestion and query services are stateless apart from their external stores and can be replicated behind a load balancer. The current in-memory rate limit becomes per-instance when ingestion scales; replace it with Redis or gateway quotas when a global limit matters.

Processing concurrency is bounded by partition count. Six partitions mean up to six active consumer threads in one group. Add replicas with `docker compose up -d --scale processing-service=3`. Kafka rebalances partitions when membership changes; polling pauses briefly, assignments move, and uncommitted work is redelivered.

At one million logs/minute, first batch producer sends and bulk OpenSearch indexing, then increase partitions after measuring key skew. Scale brokers, spread partition leaders, tune compression/batches, scale processors up to partition concurrency, and use a multi-node OpenSearch cluster with shard sizing and rollover. Monitor request latency, producer errors, lag, indexing rejection, heap, disk watermarks, and search latency.

