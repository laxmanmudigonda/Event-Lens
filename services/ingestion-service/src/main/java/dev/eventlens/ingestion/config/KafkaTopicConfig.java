package dev.eventlens.ingestion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    NewTopic rawLogsTopic(@Value("${eventlens.kafka.raw-topic}") String name,
                          @Value("${eventlens.kafka.partitions:6}") int partitions) {
        return TopicBuilder.name(name).partitions(partitions).replicas(1)
                .config("retention.ms", "604800000").build();
    }

    @Bean
    NewTopic deadLetterTopic(@Value("${eventlens.kafka.dead-letter-topic}") String name,
                             @Value("${eventlens.kafka.partitions:6}") int partitions) {
        return TopicBuilder.name(name).partitions(partitions).replicas(1)
                .config("retention.ms", "2592000000").build();
    }
}

