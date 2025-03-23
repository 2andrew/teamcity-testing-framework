package com.example.teamcity.api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import static com.example.teamcity.api.errors.CommonErrorMessages.AUTH_REQUIRED;
import static com.example.teamcity.api.errors.ProjectErrorMessages.NO_PROJECT_FOUND;

public class ValidationResponseSpecifications {
    public static ResponseSpecification checkProjectNotFound(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_NOT_FOUND);
        responseSpecBuilder.expectBody(Matchers.containsString(NO_PROJECT_FOUND.getError().formatted(projectName)));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkIdNameInUse(String error_str) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody(Matchers.containsString(error_str));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkUnAuthorized() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_UNAUTHORIZED);
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkNoPermissions(String error_str) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_FORBIDDEN);
        responseSpecBuilder.expectBody(Matchers.containsString(error_str));
        return responseSpecBuilder.build();
    }
}
