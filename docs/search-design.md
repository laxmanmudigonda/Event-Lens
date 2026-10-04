# Search design

OpenSearch is optimized for full-text retrieval and aggregations. EventLens creates daily indices and an explicit template. `message` is analyzed into an inverted index; identifiers and filter dimensions use `keyword`; timestamps use `date`; custom metadata uses `flat_object`, preventing each arbitrary key from becoming a new mapped field.

Search parameters become a server-built `bool` query. Exact constraints go into filter context, message text into `must`, and time into a range filter. Raw query DSL is not exposed. Results are sorted by timestamp. Size is 1–200 and `from + size` may not exceed 10,000 because every shard must retain and merge skipped hits. `search_after` is the recommended cursor for deep traversal.

Daily indices make retention and deletion cheap: hot recent indices stay searchable, rollover limits shard size, and an Index State Management policy can move or delete older data. The local build documents this lifecycle but does not delete data automatically.

