# Benchmarks

Results below are local verification samples, not capacity claims. The full Compose stack was run on the machine identified below.

## Reproducible procedure

1. Allocate at least 6 GB RAM to Docker Desktop and start the stack.
2. Warm it with 1,000 events.
3. Run `scripts/benchmark.ps1` for 1, 2, and 3 processing replicas.
4. Test 1,000 events, 10,000 events, then a 60-second sustained workload.
5. Record load-generator accepted/failed/throughput/mean client latency, `/api/v1/system/kafka` lag, time until lag returns to zero, and sample search latency.
6. Repeat each case three times and report median plus hardware/Docker allocation.

| Date / machine | Events | Target rate | Consumers | Accepted | Failed | Client req/s | Mean client latency | Drain time | Search latency |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 2026-10-04 / Ryzen 7 5700U, 15.3 GB RAM, Docker Desktop | 999 | 100/s for 10 s | 3 | 999 | 0 | 99.74 | 46.54 ms | <5 s (sample interval) | 482.84 ms cold sample |

## Verified local sample

- Date and machine: 2026-10-04, AMD Ryzen 7 5700U, 15.3 GB RAM, Docker Desktop
- Workload: 999 events at a target 100 requests/second for 10 seconds
- Processing consumers: 3 across 6 Kafka partitions
- Result: 999 accepted, 0 failed, 99.74 requests/second, 46.54 ms mean client latency
- Drain and storage: zero Kafka lag within the 5-second sample interval; all 999 events found in OpenSearch
- Query sample: 482.84 ms for the first filtered request after the run

This single short run is a functional baseline. Use the repeated procedure above for a defensible capacity study. Do not interpret additional consumers as linear scaling: partition/key skew, OpenSearch indexing, host CPU, and disk may dominate.

