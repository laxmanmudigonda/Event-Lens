package dev.eventlens.processing.search;

import dev.eventlens.model.LogEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenSearchIndexer {
    private final RestClient client;
    public OpenSearchIndexer(RestClient client) { this.client = client; }
    public void index(LogEvent event) {
        client.put().uri("/{index}/_doc/{id}?refresh=false", IndexName.forTimestamp(event.timestamp()), event.eventId())
                .contentType(MediaType.APPLICATION_JSON).body(document(event)).retrieve().toBodilessEntity();
    }

    private Map<String, Object> document(LogEvent event) {
        Map<String, Object> document = new LinkedHashMap<>();
        document.put("eventId", event.eventId());
        document.put("timestamp", event.timestamp().toString());
        document.put("receivedAt", event.receivedAt().toString());
        document.put("projectId", event.projectId());
        document.put("service", event.service());
        document.put("environment", event.environment());
        document.put("level", event.level().name());
        document.put("message", event.message());
        if (event.traceId() != null) document.put("traceId", event.traceId());
        if (event.requestId() != null) document.put("requestId", event.requestId());
        if (event.host() != null) document.put("host", event.host());
        document.put("metadata", event.metadata());
        return document;
    }
}

