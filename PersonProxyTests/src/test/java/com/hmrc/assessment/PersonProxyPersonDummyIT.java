package com.hmrc.assessment;

import org.junit.Test;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.equalTo;

public class PersonProxyPersonDummyIT {

    @Test
    public void shouldCallPersonDummyAPI() throws Exception {

        given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("{ \"name\" : \"Guido\" }")
        .when()
                .log().all()
                .post("http://172.16.0.64:8280/persondummy ")
        .then()
                .statusCode(200)
                .body("message", equalTo("Hello Guido!"));

    }

}
