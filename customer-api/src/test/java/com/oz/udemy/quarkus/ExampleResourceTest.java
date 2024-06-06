package com.oz.udemy.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ExampleResourceTest {

  @Test
  void testHelloEndpoint() {
    given()
        .when().get("/customer")
        .then()
        .statusCode(200)
        .body(is("[]"));
  }
}
