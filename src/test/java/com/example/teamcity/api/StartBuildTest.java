package com.example.teamcity.api;

import com.example.teamcity.api.generators.StepGenerator;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Steps;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.common.WireMock;
import io.qameta.allure.Feature;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {
    private CheckedRequests userCheckRequests;
    private Build build;
    private Build buildResult;

    @BeforeMethod
    public void setupWireMockServer() {
        var fakeBuild = Build.builder()
                .state("finished")
                .status("SUCCESS")
                .build();

        WireMock.setupServer(post(BUILD_QUEUE.getUrl()), HttpStatus.SC_OK, fakeBuild);
        WireMock.setupServer(get(urlPathMatching(BUILD_QUEUE.getUrl() + "/id%3A\\d+")), HttpStatus.SC_OK, fakeBuild);
    }

    private static void waitUntilBuildFinished(CheckedRequests req, String buildId) {
        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    Build build = (Build) req.getRequest(BUILD_QUEUE).read(buildId);
                    return build.getState().equals("finished");
                });
    }

    @BeforeMethod
    public void setupBuildData() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        createSampleBuild();
    }

    private void createSampleBuild() {
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        var buildType = testData.getBuildType();

        Steps steps = Steps.builder()
                .step(Collections.singletonList(StepGenerator.generateSampleScript()))
                .build();
        buildType.setSteps(steps);

        BuildType buildTypeResp = (BuildType) userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var buildTypeTempl = BuildType.builder()
                .id(buildTypeResp.getId())
                .build();

        var buildToRun = Build.builder()
                .buildType(buildTypeTempl)
                .build();
        build = (Build) userCheckRequests.getRequest(BUILD_QUEUE).create(buildToRun);
    }

    @Test(description = "User should be able to start build (with WireMock)",
            groups = {"Regression"})
    public void userStartsBuildWithWireMockTest() {
        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);

        buildResult = checkedBuildQueueRequest.create(Build.builder()
                .buildType(testData.getBuildType())
                .build());
    }

    @Test(description = "User should be able to start build (without WireMock) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldTest() {

        waitUntilBuildFinished(userCheckRequests, build.getId());
        buildResult = (Build) userCheckRequests.getRequest(BUILD_QUEUE).read(build.getId());
    }


    @Test(description = "User should be able to start build (with WireMock) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldWireMockTest() {

        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);
        buildResult = checkedBuildQueueRequest.read(build.getId());
    }


    @AfterMethod
    public void checkBuildResults() {
        softy.assertEquals(buildResult.getState(), "finished");
        softy.assertEquals(buildResult.getStatus(), "SUCCESS");
    }

    @AfterMethod(alwaysRun = true)
    public void stopWireMockServer() {
        WireMock.stopServer();
    }
}