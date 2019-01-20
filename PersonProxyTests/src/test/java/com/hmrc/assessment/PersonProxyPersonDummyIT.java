package com.hmrc.assessment;

import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.equalTo;

public class PersonProxyPersonDummyIT {

    private static final Logger LOG = LoggerFactory.getLogger(PersonProxyPersonDummyIT.class);

    private static final String PERSON_DUMMY_API_URL_PROPERTY = "com.hmrc.assessment.dummypersonapi.url";

    @Test
    public void shouldCallPersonDummyAPI() throws Exception {

        final String personDummyAPIUrl = System.getProperty(PERSON_DUMMY_API_URL_PROPERTY);

        LOG.info("personDummyAPIUrl: {}", personDummyAPIUrl);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("{ \"name\" : \"Guido\" }")
        .when()
                .log().all()
                .post(personDummyAPIUrl)
        .then()
                .log().all()
                .statusCode(200)
                .body("message", equalTo("Hello Guido!"));

    }

}
