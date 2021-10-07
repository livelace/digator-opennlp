package ru.livelace.digator_opennlp;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class NerModelPathTest {

    @Test
    void shouldGetSwaggerUI() {
        given()
                .when().get("/swagger-ui")
                .then()
                .statusCode(200);
    }
}
