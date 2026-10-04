# Kafka design

Kafka is the durable asynchronous boundary. `eventlens.logs.raw` retains seven days locally; `eventlens.logs.dead-letter` retains thirty. Both have six partitions and replication factor one in Compose.

The producer uses `acks=all`, retries, and idempotence. The key is `service`, so all records for one service resolve consistently to one partition while services can process in parallel. Ordering is guaranteed only within a partition, never across the full topic.

Consumers use group `eventlens-indexers`, disable auto commit, and acknowledge after OpenSearch success or durable DLT publication. With three processors, Kafka typically assigns two partitions each. When one exits, the coordinator pauses and reassigns its partitions. With seven processors, at least one is idle because six partitions are the maximum active units of work.

Lag is `log-end-offset - committed-offset`. `/api/v1/system/kafka` reports it per partition. Rising lag means producers are outpacing consumers or the sink is slow. Kafka retention also enables replay: use a new group or reset offsets after ensuring sink idempotency.

JSON was selected for approachability. Schema Registry plus Avro/Protobuf is the next step when compatibility enforcement and compact payloads justify added infrastructure.

