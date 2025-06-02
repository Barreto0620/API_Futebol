package org.senac.idempotency;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.smallrye.mutiny.Uni;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance; // Importado para injeção preguiçosa do Cache
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
import java.util.function.Function; // Function é usado pelas chamadas do cache

@Provider
@ApplicationScoped
@Priority(Priorities.HEADER_DECORATOR)
public class IdempotencyFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
    private static final String IDEMPOTENT_CONTEXT_PROPERTY = "idempotent-context";

    @Inject
    @CacheName("idempotency-cache")
    Instance<Cache> cacheInstance; // Alterado: Injeção de Instance<Cache> em vez de Cache direto

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        Class<?> clazz = resourceInfo.getResourceClass();

        Idempotent methodAnnotation = method.getAnnotation(Idempotent.class);
        Idempotent classAnnotation = clazz.getAnnotation(Idempotent.class);

        // Se nem o método nem a classe têm a anotação @Idempotent, não faz nada
        if (methodAnnotation == null && classAnnotation == null) {
            return;
        }

        Idempotent idempotentConfig = methodAnnotation != null ? methodAnnotation : classAnnotation;

        String idempotencyKey = requestContext.getHeaderString(IDEMPOTENCY_KEY_HEADER);

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            requestContext.abortWith(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("O cabeçalho X-Idempotency-Key é obrigatório para esta operação")
                    .build());
            return;
        }

        String cacheKey = createCacheKey(requestContext, idempotencyKey);
        Cache actualCache = cacheInstance.get(); // Obtém a instância real do Cache aqui

        try {
            // Acessa o cache para verificar se já existe um registro
            IdempotencyRecord record = (IdempotencyRecord) actualCache.get(
                    cacheKey,
                    k -> Uni.createFrom().nullItem() // Função para computar se a chave não existir (retorna nulo para não adicionar nada aqui)
            ).await().indefinitely();

            if (record != null) {
                // Se o registro existe, verifica a expiração (opcional, depende da estratégia de cache)
                // Para este exemplo, se existe, retorna o resultado cacheado
                requestContext.abortWith(Response
                        .status(record.getStatus())
                        .entity(record.getBody())
                        .build());
                return;
            }

            // Armazena o contexto da idempotência para uso no filtro de resposta
            requestContext.setProperty(IDEMPOTENT_CONTEXT_PROPERTY,
                    new IdempotentContext(cacheKey, idempotentConfig.expireAfter()));

        } catch (Exception e) {
            // Considerar usar um logger apropriado em vez de System.err
            System.err.println("Erro ao acessar o cache de idempotência na requisição: " + e.getMessage());
            // Em caso de erro, permite que a requisição prossiga (fail-open) ou pode decidir por fail-close
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        IdempotentContext context = (IdempotentContext) requestContext.getProperty(IDEMPOTENT_CONTEXT_PROPERTY);
        if (context == null) {
            return;
        }

        // Verifica se a resposta é bem-sucedida antes de cachear (ex: status 2xx)
        // Esta é uma decisão de design; pode-se querer cachear erros também.
        if (responseContext.getStatus() >= 200 && responseContext.getStatus() < 300) {
            IdempotencyRecord record = new IdempotencyRecord(
                    responseContext.getStatus(),
                    responseContext.getEntity(), // Cuidado ao cachear entidades que não são serializáveis ou são muito grandes
                    Instant.now().plusSeconds(context.getExpireAfter())
            );

            Cache actualCache = cacheInstance.get(); // Obtém a instância real do Cache aqui

            try {
                // Armazena o resultado no cache
                // A função fornecida será usada para calcular o valor se a chave não existir ou estiver expirada.
                // Neste caso, queremos explicitamente definir o valor.
                actualCache.get(
                        context.getCacheKey(),
                        k -> Uni.createFrom().item(record)
                ).await().indefinitely(); // await() é necessário para garantir que a operação de cache seja tentada
            } catch (Exception e) {
                // Considerar usar um logger apropriado
                System.err.println("Erro ao armazenar resultado no cache de idempotência na resposta: " + e.getMessage());
            }
        }
    }

    private String createCacheKey(ContainerRequestContext requestContext, String idempotencyKey) {
        // Considerar incluir o corpo da requisição (ou um hash dele) na chave de cache
        // se operações com o mesmo método/path/key mas corpos diferentes não forem idempotentes entre si.
        return requestContext.getMethod() + ":" +
                requestContext.getUriInfo().getPath() + ":" +
                idempotencyKey;
    }

    // Classe interna para manter o contexto entre request e response filter
    private static class IdempotentContext {
        private final String cacheKey;
        private final int expireAfter; // Segundos

        public IdempotentContext(String cacheKey, int expireAfter) {
            this.cacheKey = cacheKey;
            this.expireAfter = expireAfter;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public int getExpireAfter() {
            return expireAfter;
        }
    }

    // Classe para armazenar os dados da resposta no cache
    // Certifique-se de que esta classe e o 'body' são serializáveis se usar caches distribuídos.
    public static class IdempotencyRecord {
        private int status;
        private Object body; // Pode precisar de tratamento especial para serialização/desserialização
        private Instant expiry; // Informativo, a expiração real é gerenciada pelo cache

        public IdempotencyRecord() {
            // Construtor padrão para serialização/desserialização, se necessário
        }

        public IdempotencyRecord(int status, Object body, Instant expiry) {
            this.status = status;
            this.body = body;
            this.expiry = expiry;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getBody() {
            return body;
        }

        public void setBody(Object body) {
            this.body = body;
        }

        public Instant getExpiry() {
            return expiry;
        }

        public void setExpiry(Instant expiry) {
            this.expiry = expiry;
        }
    }
}