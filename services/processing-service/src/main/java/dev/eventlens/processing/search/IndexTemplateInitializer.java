package dev.eventlens.processing.search;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class IndexTemplateInitializer implements ApplicationRunner {
    private static final String TEMPLATE = """
        {"index_patterns":["eventlens-logs-*"],"priority":100,"template":{"settings":{"number_of_shards":3,"number_of_replicas":0,"refresh_interval":"1s"},"mappings":{"dynamic":"strict","properties":{"eventId":{"type":"keyword"},"timestamp":{"type":"date"},"receivedAt":{"type":"date"},"projectId":{"type":"keyword"},"service":{"type":"keyword"},"environment":{"type":"keyword"},"level":{"type":"keyword"},"message":{"type":"text","analyzer":"standard"},"traceId":{"type":"keyword"},"requestId":{"type":"keyword"},"host":{"type":"keyword"},"metadata":{"type":"flat_object"}}}}}
        """;
    private final RestClient client;
    public IndexTemplateInitializer(RestClient client) { this.client = client; }
    @Override public void run(ApplicationArguments args) {
        client.put().uri("/_index_template/eventlens-logs-template").contentType(MediaType.APPLICATION_JSON)
                .body(TEMPLATE).retrieve().toBodilessEntity();
    }
}

