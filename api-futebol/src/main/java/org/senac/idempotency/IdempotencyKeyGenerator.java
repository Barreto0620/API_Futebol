package org.senac.idempotency;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class IdempotencyKeyGenerator {

    /**
     * Gera uma nova chave de idempotência baseada em UUID.
     * Esta chave deve ser enviada pelo cliente no cabeçalho X-Idempotency-Key.
     *
     * @return Uma nova String UUID.
     */
    public String generateNewKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * Valida o formato de uma chave de idempotência.
     * Pode ser estendido para verificar mais critérios, se necessário.
     *
     * @param key A chave de idempotência a ser validada.
     * @return true se a chave for válida, false caso contrário.
     */
    public boolean isValidKey(String key) {
        // Implemente a lógica de validação da chave (ex: formato UUID)
        try {
            UUID.fromString(key);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}