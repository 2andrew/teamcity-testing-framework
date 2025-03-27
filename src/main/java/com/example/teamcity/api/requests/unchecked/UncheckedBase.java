package com.example.teamcity.api.requests.unchecked;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import com.example.teamcity.api.requests.SearchInterface;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UncheckedBase extends Request implements CrudInterface, SearchInterface {

    public UncheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
    }

    @Override
    @Step("Creating entity")
    public Response create(BaseModel model) {
        return RestAssured
                .given()
                .spec(spec)
                .body(model)
                .post(endpoint.getUrl());
    }

    @Override
    @Step("Performing findAll operation with attribute={attribute}")
    public Response findAll(String attribute) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl());
    }

    @Override
    @Step("Performing read operation with locator={locator}")
    public Response read(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl() + "/" + locator);
    }

    @Override
    @Step("Updating entity with locator={locator}")
    public Response update(String locator, BaseModel model) {
        return RestAssured
                .given()
                .body(model)
                .spec(spec)
                .put(endpoint.getUrl() + "/" + locator);
    }

    @Override
    @Step("Deleting entity with locator={locator}")
    public Response delete(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .delete(endpoint.getUrl() + "/" + locator);
    }

    @Override
    @Step("Performing search with searchParameter={searchParameter}")
    public Response search(String searchParameter) {
        var params = searchParameter.split(":");
        return RestAssured
                .given()
                .spec(spec)
                .pathParams(params[0], params[1])
                .get("%s/{%s}".formatted(endpoint.getUrl(), params[0]));
    }
}