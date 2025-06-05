package org.senac.idempotency;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.smallrye.mutiny.Uni;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@ApplicationScoped
@Priority(Priorities.HEADER_DECORATOR)
public class IdempotencyFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(IdempotencyFilter.class);
    private static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
    private static final String IDEMPOTENT_CONTEXT_PROPERTY = "idempotent-context";

    @Inject
    @CacheName("idempotency-cache")
    Instance<Cache> cacheInstance;

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        Class<?> clazz = resourceInfo.getResourceClass();

        Idempotent methodAnnotation = method.getAnnotation(Idempotent.class);
        Idempotent classAnnotation = clazz.getAnnotation(Idempotent.class);

        if (methodAnnotation == null && classAnnotation == null) {
            return;
        }

        Idempotent idempotentConfig = methodAnnotation != null ? methodAnnotation : classAnnotation;
        String idempotencyKey = requestContext.getHeaderString(IDEMPOTENCY_KEY_HEADER);

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            requestContext.abortWith(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("O cabeçalho X-Idempotency-Key é obrigatório para esta operação.")
                    .build());
            return;
        }

        String cacheKey = createCacheKey(requestContext, idempotencyKey);
        Cache actualCache = cacheInstance.get();

        try {
            Function<String, Uni<IdempotencyRecord>> computeIfAbsentForGet = k -> Uni.createFrom().item((IdempotencyRecord) null);
            Uni<IdempotencyRecord> recordUni = actualCache.<String, IdempotencyRecord>getAsync(cacheKey, computeIfAbsentForGet);
            IdempotencyRecord record = recordUni.await().indefinitely();

            if (record != null) {
                LOG.info("Requisição idempotente para a chave {} já processada. Retornando resposta cacheada (Status: {}).", cacheKey, record.getStatus());
                requestContext.abortWith(Response
                        .status(record.getStatus())
                        .entity(record.getBody())
                        .build());
                return;
            }

            LOG.debug("Requisição idempotente para a chave {} é nova. Prosseguindo com o processamento.", cacheKey);
            requestContext.setProperty(IDEMPOTENT_CONTEXT_PROPERTY,
                    new IdempotentContext(cacheKey, idempotentConfig.expireAfter()));

        } catch (Exception e) {
            LOG.error("Erro inesperado ao acessar o cache de idempotência na requisição para chave {}: {}", cacheKey, e.getMessage(), e);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        IdempotentContext context = (IdempotentContext) requestContext.getProperty(IDEMPOTENT_CONTEXT_PROPERTY);
        if (context == null) {
            return;
        }

        if (responseContext.getStatus() >= 200 && responseContext.getStatus() < 300) {
            Object responseBody = responseContext.getEntity();
            IdempotencyRecord record = new IdempotencyRecord(
                    responseContext.getStatus(),
                    responseBody,
                    Instant.now().plusSeconds(context.getExpireAfter())
            );

            Cache actualCache = cacheInstance.get();

            try {
                IdempotencyRecord recordToCache = record;
                Function<String, Uni<IdempotencyRecord>> computeIfAbsentForPut = k -> Uni.createFrom().item(recordToCache);
                actualCache.<String, IdempotencyRecord>getAsync(context.getCacheKey(), computeIfAbsentForPut)
                        .await().indefinitely();
                LOG.info("Resultado da requisição para a chave {} (Status: {}) cacheado com sucesso.", context.getCacheKey(), record.getStatus());
            } catch (Exception e) {
                LOG.error("Erro ao armazenar resultado no cache de idempotência na resposta para chave {}: {}", context.getCacheKey(), e.getMessage(), e);
            }
        } else {
            LOG.debug("Resposta para chave {} não é 2xx (Status: {}). Não cacheando.", context.getCacheKey(), responseContext.getStatus());
            requestContext.removeProperty(IDEMPOTENT_CONTEXT_PROPERTY);
        }
    }

    private String createCacheKey(ContainerRequestContext requestContext, String idempotencyKey) {
        return requestContext.getMethod() + ":" +
               requestContext.getUriInfo().getPath() + ":" +
               idempotencyKey;
    }

    private static class IdempotentContext {
        private final String cacheKey;
        private final int expireAfter;

        public IdempotentContext(String cacheKey, int expireAfter) {
            this.cacheKey = cacheKey;
            this.expireAfter = expireAfter;
        }

        public String getCacheKey() { return cacheKey; }
        public int getExpireAfter() { return expireAfter; }
    }

    public static class IdempotencyRecord {
        private int status;
        private Object body;
        private Instant expiry;

        public IdempotencyRecord() {}

        public IdempotencyRecord(int status, Object body, Instant expiry) {
            this.status = status;
            this.body = body;
            this.expiry = expiry;
        }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public Object getBody() { return body; }
        public void setBody(Object body) { this.body = body; }
        public Instant getExpiry() { return expiry; }
        public void setExpiry(Instant expiry) { this.expiry = expiry; }
    }
}