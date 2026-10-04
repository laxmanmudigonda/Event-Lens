package dev.eventlens.ingestion.service;

import dev.eventlens.model.*;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogIngestionService {
    private final KafkaTemplate<String, LogEvent> kafka;
    private final String topic;
    private final Clock clock;
    public LogIngestionService(KafkaTemplate<String, LogEvent> kafka,
            @Value("${eventlens.kafka.raw-topic}") String topic, Clock clock) {
        this.kafka = kafka; this.topic = topic; this.clock = clock;
    }
    public CompletableFuture<AcceptedEvent> ingest(LogRequest request, String projectId) {
        LogEvent event = EventFactory.from(request, projectId, clock);
        return kafka.send(topic, event.service(), event).thenApply(result -> AcceptedEvent.accepted(event.eventId()));
    }
    public CompletableFuture<List<AcceptedEvent>> ingestBatch(List<LogRequest> requests, String projectId) {
        var futures = requests.stream().map(request -> ingest(request, projectId)).toList();
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> futures.stream().map(CompletableFuture::join).toList());
    }
}

