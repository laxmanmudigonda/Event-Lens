package dev.eventlens.ingestion.security;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
class ApiKeyHasherTest {
    @Test void hashesDeterministicallyWithoutStoringRawValue() {
        var hasher = new ApiKeyHasher();
        assertEquals(64, hasher.hash("evl_test").length());
        assertEquals(hasher.hash("evl_test"), hasher.hash("evl_test"));
        assertNotEquals("evl_test", hasher.hash("evl_test"));
    }
}
