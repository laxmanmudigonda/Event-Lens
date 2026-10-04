package dev.eventlens.query.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dev.eventlens.model.LogLevel;
import dev.eventlens.query.search.*;
import java.time.Instant;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SearchController {
    private final OpenSearchGateway search;
    public SearchController(OpenSearchGateway search) { this.search = search; }
    @GetMapping("/search")
    public OpenSearchGateway.SearchResult search(@RequestParam(required=false) String q,
            @RequestParam(required=false) String service, @RequestParam(required=false) String environment,
            @RequestParam(required=false) LogLevel level, @RequestParam(required=false) Instant from,
            @RequestParam(required=false) Instant to, @RequestParam(required=false) String traceId,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="50") int size,
            @RequestParam(defaultValue="desc") String sort) {
        return search.search(new SearchCriteria(q, service, environment, level, from, to, traceId, page, size, sort));
    }
    @GetMapping("/analytics/overview") public JsonNode overview() { return search.analytics("1m"); }
    @GetMapping("/analytics/services") public JsonNode services() { return search.analytics("1m").path("aggregations").path("services"); }
    @GetMapping("/analytics/levels") public JsonNode levels() { return search.analytics("1m").path("aggregations").path("levels"); }
    @GetMapping("/analytics/timeline") public JsonNode timeline(@RequestParam(defaultValue="1m") String interval) {
        if (!java.util.Set.of("1m", "5m", "1h").contains(interval)) throw new IllegalArgumentException("interval must be 1m, 5m, or 1h");
        return search.analytics(interval).path("aggregations").path("timeline");
    }
}

