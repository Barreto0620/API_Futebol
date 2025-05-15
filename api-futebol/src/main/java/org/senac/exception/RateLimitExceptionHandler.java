package org.senac.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

@Provider
public class RateLimitExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof BulkheadException) {
            return Response.status(429)
                .entity("Taxa limite excedida. Por favor, tente novamente mais tarde.")
                .build();
        } else if (exception instanceof TimeoutException) {
            return Response.status(503)
                .entity("O serviço está demorando mais que o esperado. Por favor, tente novamente.")
                .build();
        } else if (exception instanceof CircuitBreakerOpenException) {
            return Response.status(503)
                .entity("O serviço está temporariamente indisponível. Por favor, tente novamente em alguns minutos.")
                .build();
        }
        
        return Response.status(500)
            .entity("Ocorreu um erro interno. Por favor, tente novamente mais tarde.")
            .build();
    }
}