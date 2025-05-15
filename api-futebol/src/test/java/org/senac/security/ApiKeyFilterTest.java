package org.senac.security;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ApiKeyFilterTest {

    @Test
    public void testProtectedEndpointWithValidApiKey() {
        given()
            .header("X-API-Key", "your-secret-api-key-here")
            .when().get("/times")
            .then()
            .statusCode(200);
    }

    @Test
    public void testProtectedEndpointWithInvalidApiKey() {
        given()
            .header("X-API-Key", "invalid-key")
            .when().get("/times")
            .then()
            .statusCode(401);
    }

    @Test
    public void testProtectedEndpointWithoutApiKey() {
        given()
            .when().get("/times")
            .then()
            .statusCode(401);
    }

    @Test
    public void testPublicEndpoint() {
        given()
            .when().get("/q/health")
            .then()
            .statusCode(200);
    }
}