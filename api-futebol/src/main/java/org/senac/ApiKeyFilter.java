package org.senac;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger; // Se você estiver usando JBoss Logging (comum no Quarkus)

@Provider
public class ApiKeyFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(ApiKeyFilter.class);

    // A chave de API esperada. Em um ambiente real, isso viria do application.properties
    // ou de um serviço de configuração.
    private static final String EXPECTED_API_KEY = "senhafortedeseguranca"; // <--- Use a mesma chave que no test

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        LOG.info("Request received for path: " + path);

        // Exclui o endpoint Swagger/OpenAPI da validação da API Key
        if (path.startsWith("q/swagger-ui") || path.startsWith("q/openapi")) {
            return; // Não aplica o filtro para essas rotas
        }

        String apiKey = requestContext.getHeaderString("X-API-Key"); // Assumindo o cabeçalho X-API-Key

        if (apiKey == null || !apiKey.equals(EXPECTED_API_KEY)) {
            LOG.warn("Invalid or missing API Key. Provided Key: " + apiKey);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                             .entity("Unauthorized - Missing or Invalid API Key")
                                             .build());
        }
        // Se a chave for válida, a requisição continua normalmente.
    }
}