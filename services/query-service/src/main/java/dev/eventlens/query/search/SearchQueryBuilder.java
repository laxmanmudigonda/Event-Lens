package dev.eventlens.query.search;

import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class SearchQueryBuilder {
    public Map<String, Object> build(SearchCriteria criteria) {
        if (criteria.page() < 0 || criteria.size() < 1 || criteria.size() > 200) throw new IllegalArgumentException("size must be 1..200 and page must be non-negative");
        if ((long) criteria.page() * criteria.size() + criteria.size() > 10_000) throw new IllegalArgumentException("Deep pagination beyond 10,000 results is not supported");
        List<Map<String, Object>> must = new ArrayList<>();
        List<Map<String, Object>> filter = new ArrayList<>();
        if (text(criteria.q())) must.add(Map.of("match", Map.of("message", criteria.q())));
        exact(filter, "service", criteria.service()); exact(filter, "environment", criteria.environment());
        exact(filter, "level", criteria.level() == null ? null : criteria.level().name()); exact(filter, "traceId", criteria.traceId());
        if (criteria.from() != null || criteria.to() != null) {
            Map<String, Object> bounds = new LinkedHashMap<>();
            if (criteria.from() != null) bounds.put("gte", criteria.from().toString());
            if (criteria.to() != null) bounds.put("lte", criteria.to().toString());
            filter.add(Map.of("range", Map.of("timestamp", bounds)));
        }
        Map<String, Object> bool = new LinkedHashMap<>();
        if (!must.isEmpty()) bool.put("must", must);
        if (!filter.isEmpty()) bool.put("filter", filter);
        String direction = "asc".equalsIgnoreCase(criteria.sort()) ? "asc" : "desc";
        return Map.of("from", criteria.page() * criteria.size(), "size", criteria.size(),
                "track_total_hits", true,
                "query", bool.isEmpty() ? Map.of("match_all", Map.of()) : Map.of("bool", bool),
                "sort", List.of(Map.of("timestamp", Map.of("order", direction))));
    }
    private void exact(List<Map<String, Object>> filters, String field, String value) {
        if (text(value)) filters.add(Map.of("term", Map.of(field, value)));
    }
    private boolean text(String value) { return value != null && !value.isBlank(); }
}

