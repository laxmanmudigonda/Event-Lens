package dev.eventlens.processing.kafka;

import dev.eventlens.model.*;
import dev.eventlens.processing.search.OpenSearchIndexer;
import java.time.Instant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class LogConsumer {
    private static final Logger log = LoggerFactory.getLogger(LogConsumer.class);
    private final OpenSearchIndexer indexer;
    private final KafkaTemplate<String, Object> kafka;
    private final String deadLetterTopic;
    private final int maxAttempts;
    private final long backoffMs;
    public LogConsumer(OpenSearchIndexer indexer, KafkaTemplate<String, Object> kafka,
            @Value("${eventlens.kafka.dead-letter-topic}") String deadLetterTopic,
            @Value("${eventlens.processing.max-attempts:3}") int maxAttempts,
            @Value("${eventlens.processing.backoff-ms:1000}") long backoffMs) {
        this.indexer = indexer; this.kafka = kafka; this.deadLetterTopic = deadLetterTopic;
        this.maxAttempts = maxAttempts; this.backoffMs = backoffMs;
    }

    @KafkaListener(topics = "${eventlens.kafka.raw-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, LogEvent> record, Acknowledgment acknowledgment) {
        Exception last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                indexer.index(record.value());
                acknowledgment.acknowledge();
                log.info("indexed eventId={} service={} partition={} offset={}", record.value().eventId(),
                        record.value().service(), record.partition(), record.offset());
                return;
            } catch (Exception ex) {
                last = ex;
                log.warn("index attempt {}/{} failed for eventId={}: {}", attempt, maxAttempts,
                        record.value().eventId(), ex.getMessage());
                backoff(attempt);
            }
        }
        var deadLetter = new DeadLetterEvent(record.value(), last.getClass().getName(), safeMessage(last),
                record.topic(), record.partition(), record.offset(), Instant.now(), maxAttempts);
        kafka.send(deadLetterTopic, record.key(), deadLetter).join();
        acknowledgment.acknowledge();
        log.error("dead-lettered eventId={} after {} attempts", record.value().eventId(), maxAttempts);
    }

    private void backoff(int attempt) {
        try { Thread.sleep(backoffMs * attempt); }
        catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); throw new IllegalStateException("Retry interrupted", interrupted); }
    }
    private String safeMessage(Exception ex) {
        String message = ex.getMessage() == null ? "No message" : ex.getMessage();
        return message.substring(0, Math.min(message.length(), 1000));
    }
}

