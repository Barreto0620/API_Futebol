package org.senac.idempotency;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Optional;

@Provider
@ApplicationScoped
@Priority(Priorities.USER)
public class IdempotencyFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";
    private static final String CACHE_NAME = "idempotency-cache";

    @Inject
    CacheManager cacheManager;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String idempotencyKey = requestContext.getHeaderString(IDEMPOTENCY_HEADER);
        
        if (idempotencyKey == null || requestContext.getMethod().equals("GET")) {
            return;
        }

        Optional<Cache> cache = cacheManager.getCache(CACHE_NAME);
        if (cache.isPresent()) {
            Optional<Response> cachedResponse = cache.get().get(idempotencyKey);
            if (cachedResponse.isPresent()) {
                requestContext.abortWith(cachedResponse.get());
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String idempotencyKey = requestContext.getHeaderString(IDEMPOTENCY_HEADER);
        
        if (idempotencyKey == null || requestContext.getMethod().equals("GET")) {
            return;
        }

        Optional<Cache> cache = cacheManager.getCache(CACHE_NAME);
        if (cache.isPresent() && responseContext.getStatus() < 500) {
            cache.get().put(idempotencyKey, Response.fromResponse(responseContext.getResponse()).build());
        }
    }
}