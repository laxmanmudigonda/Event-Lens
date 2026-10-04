# ADR 003: Partition logs by service

## Context
We need useful ordering and parallelism.
## Decision
Use service name as the Kafka record key across six partitions.
## Alternatives
Random keys maximize spread but lose per-service order; trace ID groups a request but can spread one service; project ID may create hot tenants.
## Trade-offs
A very hot service can create partition skew.
## Consequences
One service is ordered within a partition and different services can progress concurrently; six partitions cap active group concurrency.

