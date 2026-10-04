package dev.eventlens.model;

public record AcceptedEvent(String eventId, String status) {
    public static AcceptedEvent accepted(String eventId) {
        return new AcceptedEvent(eventId, "accepted");
    }
}

