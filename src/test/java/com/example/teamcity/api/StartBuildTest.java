package com.example.teamcity.api;

import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.requests.CheckedRequests;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static com.example.teamcity.api.custom.AsyncConditions.waitUntilBuildFinished;
import static com.example.teamcity.api.enums.Endpoint.BUILD_QUEUE;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {


    @Test(description = "User should be able to start build (without MockServer) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldTest() {
        CheckedRequests userCheckRequests = setupBuildData();
        Build build = generateBuild(userCheckRequests);

        waitUntilBuildFinished(userCheckRequests, build.getId());

        Build buildResult = (Build) userCheckRequests.getRequest(BUILD_QUEUE).read("id:" + build.getId());
        checkBuildResults(buildResult);
    }
}