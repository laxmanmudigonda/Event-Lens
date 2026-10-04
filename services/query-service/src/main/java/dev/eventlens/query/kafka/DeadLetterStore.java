package dev.eventlens.query.kafka;
import dev.eventlens.model.DeadLetterEvent; import java.util.*; import java.util.concurrent.ConcurrentLinkedDeque; import org.springframework.beans.factory.annotation.Value; import org.springframework.kafka.annotation.KafkaListener; import org.springframework.kafka.core.KafkaTemplate; import org.springframework.stereotype.Component;
@Component
public class DeadLetterStore {
    private final Deque<DeadLetterEvent> events=new ConcurrentLinkedDeque<>(); private final KafkaTemplate<String,Object> kafka; private final String rawTopic;
    public DeadLetterStore(KafkaTemplate<String,Object> kafka,@Value("${eventlens.kafka.raw-topic}") String rawTopic){this.kafka=kafka;this.rawTopic=rawTopic;}
    @KafkaListener(topics="${eventlens.kafka.dead-letter-topic}",groupId="eventlens-dlt-inspector")
    void receive(DeadLetterEvent event){events.addFirst(event);while(events.size()>500)events.removeLast();}
    public List<DeadLetterEvent> recent(){return List.copyOf(events);}
    public void replay(String eventId){var found=events.stream().filter(e->e.originalEvent().eventId().equals(eventId)).findFirst().orElseThrow(()->new NoSuchElementException("Dead-letter event not found"));kafka.send(rawTopic,found.originalEvent().service(),found.originalEvent()).join();}
}

