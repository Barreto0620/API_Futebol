package org.senac.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.time.Duration;

@Provider
@ApplicationScoped
@Priority(Priorities.USER + 100) // Execute after regular IdempotencyFilter
public class RedisIdempotencyFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String IDEMPOTENCY_HEADER = "X-Idempotency-Key";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    @Inject
    RedisClient redisClient;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String idempotencyKey = requestContext.getHeaderString(IDEMPOTENCY_HEADER);
        
        if (idempotencyKey == null || requestContext.getMethod().equals("GET")) {
            return;
        }

        Response cachedResponse = redisClient.get(idempotencyKey);
        if (cachedResponse != null) {
            String cachedJson = cachedResponse.toString();
            jakarta.ws.rs.core.Response response = objectMapper.readValue(cachedJson, jakarta.ws.rs.core.Response.class);
            requestContext.abortWith(response);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String idempotencyKey = requestContext.getHeaderString(IDEMPOTENCY_HEADER);
        
        if (idempotencyKey == null || requestContext.getMethod().equals("GET")) {
            return;
        }

        if (responseContext.getStatus() < 500) {
            String responseJson = objectMapper.writeValueAsString(responseContext.getEntity());
            redisClient.setex(idempotencyKey, DEFAULT_TTL.getSeconds(), responseJson);
        }
    }
}