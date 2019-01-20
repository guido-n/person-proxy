package com.hmrc.assessment;

import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.equalTo;

public class PersonProxyPersonDummyIT {

    private static final Logger LOG = LoggerFactory.getLogger(PersonProxyPersonDummyIT.class);

    private static final int WIREMOCK_PORT = 42569;

    private static final String PERSON_DUMMY_API_URL_PROPERTY = "com.hmrc.assessment.dummypersonapi.url";

    @BeforeAll
    public static void connectToWiremock() {
        WireMock.configureFor(WIREMOCK_PORT);
    }

    @BeforeEach
    public void resetWireMock() {
        WireMock.resetAllRequests();
        WireMock.removeAllMappings();
    }

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

    @Test
    public void shouldSetupWireMock() {

        stubFor(post(urlEqualTo("/external/person"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><result>all good</result>")));

        given()
                .header("Accept", "application/xml")
        .when()
                .log().all()
                .post("http://localhost:" + WIREMOCK_PORT + "/external/person")
        .then()
                .log().all()
                .statusCode(200)
                .body("result", equalTo("all good"));

    }

}
