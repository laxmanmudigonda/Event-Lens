package dev.eventlens.query.search;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
public class OpenSearchGateway {
    public record SearchResult(long total, int page, int size, List<JsonNode> events) {}
    private final RestClient client; private final SearchQueryBuilder builder;
    public OpenSearchGateway(RestClient client, SearchQueryBuilder builder) { this.client = client; this.builder = builder; }
    public SearchResult search(SearchCriteria criteria) {
        JsonNode root = post("/eventlens-logs-*/_search?ignore_unavailable=true", builder.build(criteria));
        List<JsonNode> events = new ArrayList<>();
        root.path("hits").path("hits").forEach(hit -> events.add(hit.path("_source")));
        return new SearchResult(root.path("hits").path("total").path("value").asLong(), criteria.page(), criteria.size(), events);
    }
    public long count(String service, String level, Instant from) {
        List<Map<String, Object>> filters = List.of(Map.of("term", Map.of("service", service)),
                Map.of("term", Map.of("level", level)), Map.of("range", Map.of("timestamp", Map.of("gte", from.toString()))));
        JsonNode root = post("/eventlens-logs-*/_count?ignore_unavailable=true", Map.of("query", Map.of("bool", Map.of("filter", filters))));
        return root.path("count").asLong();
    }
    public JsonNode analytics(String interval) {
        Map<String, Object> body = Map.of("size", 0, "track_total_hits", true,
                "aggs", Map.of(
                        "levels", Map.of("terms", Map.of("field", "level", "size", 20)),
                        "services", Map.of("terms", Map.of("field", "service", "size", 100)),
                        "timeline", Map.of("date_histogram", Map.of("field", "timestamp", "fixed_interval", interval, "min_doc_count", 0),
                                "aggs", Map.of("errors", Map.of("filter", Map.of("terms", Map.of("level", List.of("ERROR", "FATAL"))))))));
        return post("/eventlens-logs-*/_search?ignore_unavailable=true", body);
    }
    private JsonNode post(String uri, Object body) {
        try {
            return client.post().uri(uri).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(JsonNode.class);
        } catch (HttpClientErrorException.NotFound absent) {
            return com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode();
        }
    }
}

