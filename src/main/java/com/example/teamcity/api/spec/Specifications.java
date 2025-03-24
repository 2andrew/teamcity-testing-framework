package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.User;
import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.nio.file.Paths;

import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;


public class Specifications {
    private static Specifications spec;

    private static RequestSpecBuilder reqBuilder() {
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.setBaseUri("http://" + Config.getProperty("host"));
        reqBuilder.addFilter(new RequestLoggingFilter());
        reqBuilder.addFilter(new ResponseLoggingFilter());
        reqBuilder.addFilter(new SwaggerCoverageRestAssured(
                new FileSystemOutputWriter(
                        Paths.get("target/" + OUTPUT_DIRECTORY)
                )
        ));
        reqBuilder.addFilter(new AllureRestAssured());
        reqBuilder.setContentType(ContentType.JSON);
        reqBuilder.setAccept(ContentType.JSON);
        return reqBuilder;
    }

    public static RequestSpecification superUserSpec() {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s/httpAuth".formatted("", Config.getProperty("superUserToken"), Config.getProperty("host")));
        return requestBuilder.build();
    }

    public static RequestSpecification unauthSpec() {
        var requestBuilder = reqBuilder();
        return requestBuilder.build();
    }

    public static RequestSpecification authSpec(User user) {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s".formatted(user.getUsername(), user.getPassword(), Config.getProperty("host")));
        return requestBuilder.build();
    }

    public static RequestSpecification mockSpec() {
        return reqBuilder()
                .setBaseUri("http://" + Config.getProperty("host").split(":")[0] + ":" + Config.getProperty("mockServerPort"))
                .build();
    }
}
