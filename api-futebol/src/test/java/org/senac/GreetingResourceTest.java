package org.senac;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    // Defina sua chave de API para testes aqui.
    // IMPORTANTE: Substitua "sua_chave_secreta_de_teste" pela chave que seu ApiKeyFilter espera.
    // Essa chave deve ser a mesma configurada no seu ApiKeyFilter para testes.
    private static final String TEST_API_KEY = "senhafortedeseguranca";

    @Test
    public void testEndpointTimes() {
        
        given()
          .header("X-API-Key", TEST_API_KEY) // Adiciona o cabeçalho X-API-Key
          .when().get("/times")
          .then()
             .statusCode(200); // Espera 200 OK
    }

    @Test
    public void testEndpointJogadores() {
        given()
          .header("X-API-Key", TEST_API_KEY) // Adiciona o cabeçalho X-API-Key
          .when().get("/jogadores")
          .then()
             .statusCode(200); // Espera 200 OK
    }

    @Test
    public void testEndpointPartidas() {
        given()
          .header("X-API-Key", TEST_API_KEY) // Adiciona o cabeçalho X-API-Key
          .when().get("/partidas")
          .then()
             .statusCode(200); // Espera 200 OK
    }

    @Test
    public void testEndpointDestaques() {
        given()
          .header("X-API-Key", TEST_API_KEY) // Adiciona o cabeçalho X-API-Key
          .when().get("/destaques")
          .then()
             .statusCode(200); // Espera 200 OK
    }
}