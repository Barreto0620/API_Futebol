package org.senac.idempotency;

import java.time.Instant;
import java.util.UUID;

/**
 * Utilitário para trabalhar com chaves de idempotência
 */
public class IdempotencyKeyGenerator {

    /**
     * Gera uma chave de idempotência baseada em UUID
     */
    public static String generateKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gera uma chave de idempotência determinística baseada no conteúdo
     * Útil quando o cliente precisa regenerar a mesma chave para a mesma operação
     * Ex: generateDeterministicKey("pedido123", "criar", "clienteXYZ")
     */
    public static String generateDeterministicKey(String entityId, String operation, String clientId) {
        return UUID.nameUUIDFromBytes(
            (entityId + "-" + operation + "-" + clientId).getBytes()
        ).toString();
    }

    /**
     * Gera uma chave de idempotência que inclui timestamp
     * Útil para depuração e rastreamento
     * Ex: generateTimeBasedKey("transacao") -> "transacao-1678886400000-abcd1234"
     */
    public static String generateTimeBasedKey(String prefix) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String random = UUID.randomUUID().toString().substring(0, 8); // Pega apenas uma parte para simplicidade
        return prefix + "-" + timestamp + "-" + random;
    }
}