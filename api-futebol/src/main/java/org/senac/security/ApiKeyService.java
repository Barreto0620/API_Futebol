package org.senac.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ApiKeyService {
    @ConfigProperty(name = "quarkus.api-key.value")
    String configuredApiKey;

    public boolean isValid(String apiKey) {
        return apiKey != null && apiKey.equals(configuredApiKey);
    }

    public ApiKeyInfo getApiKeyInfo(String apiKey) {
        if (isValid(apiKey)) {
            return new ApiKeyInfo("default-user", new String[]{"user"});
        }
        return null;
    }

    public static class ApiKeyInfo {
        private final String username;
        private final String[] roles;

        public ApiKeyInfo(String username, String[] roles) {
            this.username = username;
            this.roles = roles;
        }

        public String getUsername() {
            return username;
        }

        public String[] getRoles() {
            return roles;
        }
    }
}