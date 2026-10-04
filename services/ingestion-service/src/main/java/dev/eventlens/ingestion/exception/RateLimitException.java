package dev.eventlens.ingestion.exception;
public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) { super(message); }
}

