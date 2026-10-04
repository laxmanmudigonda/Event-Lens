package dev.eventlens.processing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
@EnableKafka
@SpringBootApplication
public class ProcessingApplication {
    public static void main(String[] args) { SpringApplication.run(ProcessingApplication.class, args); }
}

