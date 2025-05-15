package org.senac.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@ApplicationScoped
public class ApiKeyFilter implements ContainerRequestFilter {
    @ConfigProperty(name = "quarkus.api-key.value")
    String apiKey;

    @ConfigProperty(name = "quarkus.api-key.header-name", defaultValue = "X-API-Key")
    String apiKeyHeader;

    @Override
    public void filter(ContainerRequestContext ctx) {
        if (isPublicRoute(ctx.getUriInfo().getPath())) return;

        String providedKey = ctx.getHeaderString(apiKeyHeader);
        if (providedKey == null || !providedKey.equals(apiKey)) {
            ctx.abortWith(Response
                .status(Response.Status.UNAUTHORIZED)
                .entity("Invalid or missing API key")
                .build());
        }
    }

    private boolean isPublicRoute(String path) {
        return path.contains("/public/") 
            || path.startsWith("/health") 
            || path.startsWith("/metrics")
            || path.startsWith("/q/"); // Allow access to Swagger UI and dev console
    }
}