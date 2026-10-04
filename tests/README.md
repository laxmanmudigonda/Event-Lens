# System verification

Executable PowerShell checks live in `scripts/`:

- `smoke-test.ps1` proves one event traverses ingestion, Kafka, a processor, OpenSearch, and query.
- `duplicate-test.ps1` sends the same ID twice and checks for one search document.
- `failure-demo.ps1` exercises bounded retries and the dead-letter path.
- `benchmark.ps1` scales processors, generates a controlled workload, and prints partition lag.

Run these only after `docker compose up --build -d` reports healthy services. Unit tests remain beside their Java modules.
