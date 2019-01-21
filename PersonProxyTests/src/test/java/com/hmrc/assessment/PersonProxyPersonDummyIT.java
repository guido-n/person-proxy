package com.hmrc.assessment;

import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static java.net.HttpURLConnection.*;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

public class PersonProxyPersonDummyIT {

    private static final Logger LOG = LoggerFactory.getLogger(PersonProxyPersonDummyIT.class);

    private static final int WIREMOCK_PORT = 42569;

    private static final String WIREMOCK_URL_PATH = "/external/person";

    private static final String PERSON_DUMMY_API_URL_PROPERTY = "com.hmrc.assessment.dummypersonapi.url";
    private static final String PERSONPROXY_SERVICE__URL_PROPERTY = "com.hmrc.assessment.personproxyservice.url";

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
                .post(personDummyAPIUrl)
        .then()
                .statusCode(HTTP_OK)
                .body("message", equalTo("Hello Guido!"));

    }

    @Test
    public void shouldSetupWireMock() {

        stubFor(post(urlEqualTo(WIREMOCK_URL_PATH))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><result>all good</result>")));

        given()
                .header("Accept", "application/xml")
        .when()
                .post("http://localhost:" + WIREMOCK_PORT + WIREMOCK_URL_PATH)
        .then()
                .statusCode(HTTP_OK)
                .body("result", equalTo("all good"));

    }

    @Test
    public void shouldTestProxyService() throws URISyntaxException, IOException {

        final String personProxyUrl = System.getProperty(PERSONPROXY_SERVICE__URL_PROPERTY);

        stubFor(post(urlEqualTo(WIREMOCK_URL_PATH))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><result>all good</result>")));

        String request = new String(
                Files.readAllBytes( Paths.get(getClass().getResource("/person-request.xml").toURI()) ),
                StandardCharsets.UTF_8
        );

        given()
                .header("Accept", "application/xml")
                .header("Content-Type", "application/xml")
                .body(request)
        .when()
                .post(personProxyUrl)
        .then()
                .statusCode(HTTP_OK)
                .body("result", equalTo("all good"));

        verify(
                exactly(1),
                postRequestedFor(urlEqualTo(WIREMOCK_URL_PATH))
                        .withRequestBody(containing("<postcode>BS15 87L</postcode>"))
        );

    }

    @Test
    public void shouldTestProxyServiceWithBadRequest() throws URISyntaxException, IOException {

        final String personProxyUrl = System.getProperty(PERSONPROXY_SERVICE__URL_PROPERTY);

        stubFor(post(urlEqualTo(WIREMOCK_URL_PATH))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><result>all good</result>")));

        String request = new String(
                Files.readAllBytes( Paths.get(getClass().getResource("/person-request-bad.xml").toURI()) ),
                StandardCharsets.UTF_8
        );

        given()
                .header("Accept", "application/xml")
                .header("Content-Type", "application/xml")
                .body(request)
        .when()
                .post(personProxyUrl)
        .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body(containsString("Invalid request"));

        verify(
                exactly(0),
                postRequestedFor(urlEqualTo(WIREMOCK_URL_PATH))
        );

    }
}
