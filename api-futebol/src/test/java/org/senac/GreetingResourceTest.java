package org.senac;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class GreetingResourceTest {

    @Test
    void testGivenTimesEndpoint() {
        given()
                .when().get("/times")
                .then()
                .statusCode(200); // Retorno esperado para GET /times
    }

    @Test
    void testGivenJogadoresEndpoint() {
        given()
                .when().get("/jogadores")
                .then()
                .statusCode(200);
    }

    @Test
    void testGivenPartidasEndpoint() {
        given()
                .when().get("/partidas")
                .then()
                .statusCode(200);
    }

    @Test
    void testGivenDestaquesEndpoint() {
        given()
                .when().get("/destaques")
                .then()
                .statusCode(200);
    }
}
