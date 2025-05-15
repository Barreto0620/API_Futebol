package org.senac.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.core.MediaType;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import java.util.concurrent.TimeUnit;

@QuarkusTest
public class TimeResourceTest {

    @Test
    public void testRateLimitExceeded() throws InterruptedException {
        // Fazer 51 requisições (acima do limite de 50)
        for (int i = 0; i < 51; i++) {
            if (i < 50) {
                given()
                    .header("X-API-Key", "your-secret-api-key-here")
                    .when().get("/times")
                    .then()
                    .statusCode(200);
            } else {
                given()
                    .header("X-API-Key", "your-secret-api-key-here")
                    .when().get("/times")
                    .then()
                    .statusCode(429);
            }
        }
    }

    @Test
    public void testRateLimitReset() throws InterruptedException {
        // Primeira requisição (deve passar)
        given()
            .header("X-API-Key", "your-secret-api-key-here")
            .when().get("/times")
            .then()
            .statusCode(200);

        // Esperar 1 minuto para o limite resetar
        TimeUnit.MINUTES.sleep(1);

        // Nova requisição (deve passar novamente)
        given()
            .header("X-API-Key", "your-secret-api-key-here")
            .when().get("/times")
            .then()
            .statusCode(200);
    }
}