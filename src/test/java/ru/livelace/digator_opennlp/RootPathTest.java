package ru.livelace.digator_opennlp;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class RootPathTest {

    @Test
    void shouldGetRootIndexPage() {
        given()
                .when()
                .get("/")
                .then()
                .statusCode(302);
    }
}
