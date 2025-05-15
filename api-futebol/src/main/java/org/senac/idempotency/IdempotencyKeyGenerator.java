package org.senac.idempotency;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class IdempotencyKeyGenerator {

    public String generateKey() {
        return UUID.randomUUID().toString();
    }

    public String generateDeterministicKey(String entityId, String operation, String clientId) {
        return String.format("%s-%s-%s", entityId, operation, clientId);
    }

    public String generateTimeBasedKey(String prefix) {
        return String.format("%s-%s-%s", 
            prefix,
            Instant.now().toEpochMilli(),
            UUID.randomUUID().toString().substring(0, 8)
        );
    }
}