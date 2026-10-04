package dev.eventlens.query.controller;
import dev.eventlens.model.DeadLetterEvent; import dev.eventlens.query.kafka.DeadLetterStore; import java.util.List; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/dead-letters")
public class DeadLetterController { private final DeadLetterStore store; public DeadLetterController(DeadLetterStore store){this.store=store;} @GetMapping public List<DeadLetterEvent> list(){return store.recent();} @PostMapping("/{eventId}/replay") public void replay(@PathVariable String eventId){store.replay(eventId);} }

