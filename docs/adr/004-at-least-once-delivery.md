# ADR 004: At-least-once delivery with idempotent sink

## Context
Committing before indexing can lose logs; failures between indexing and commit can duplicate delivery.
## Decision
Disable auto commit, acknowledge after indexing/DLT, and use event ID as the OpenSearch document ID.
## Alternatives
At-most-once risks loss; claimed end-to-end exactly-once is not available across Kafka and OpenSearch without additional protocol.
## Trade-offs
The index operation may repeat.
## Consequences
Recovery favors no silent loss and duplicate effects collapse at OpenSearch.

