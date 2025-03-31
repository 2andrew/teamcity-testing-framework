package com.example.teamcity.api;

import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.requests.CheckedRequests;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static com.example.teamcity.api.custom.AsyncConditions.waitUntilBuildFinishedByType;
import static com.example.teamcity.api.enums.Endpoint.BUILD_QUEUE;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {


    @Test(description = "User should be able to start build (without MockServer) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldTest() {
        CheckedRequests userCheckRequests = setupBuildData();
        generateBuild(userCheckRequests);

        String buildId = waitUntilBuildFinishedByType(userCheckRequests, testData.getBuildType());

        Build buildResult = (Build) userCheckRequests.getRequest(BUILD_QUEUE).read("id:" + buildId);
        checkBuildResults(buildResult);
    }
}