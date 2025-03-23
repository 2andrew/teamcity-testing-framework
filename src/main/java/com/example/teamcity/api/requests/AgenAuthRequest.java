package com.example.teamcity.api.requests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class AgenAuthRequest {
    private static final String AGENT_AUTH_URL = "/app/rest/agents/name:%s/authorized";
    private RequestSpecification spec;

    public AgenAuthRequest(RequestSpecification spec) {
        this.spec = spec;
    }

    public void changeState(String name, Boolean state) {
        RestAssured.given()
                .spec(spec)
                .accept(ContentType.ANY)
                .contentType(ContentType.TEXT)
                .body(state)
                .put(AGENT_AUTH_URL.formatted(name))
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body(Matchers.containsString("true"));
        ;
    }
}
