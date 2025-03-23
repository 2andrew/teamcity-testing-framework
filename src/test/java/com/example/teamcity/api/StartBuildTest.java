package com.example.teamcity.api;

import com.example.teamcity.api.generators.SampleBuildGenerator;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.common.WireMock;
import io.qameta.allure.Feature;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.example.teamcity.api.custom.AsyncConditions.waitUntilBuildFinished;
import static com.example.teamcity.api.enums.Endpoint.BUILD_QUEUE;
import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {
    private CheckedRequests userCheckRequests;

    @BeforeClass
    public void setupWireMockServer() {
        var fakeBuild = Build.builder()
                .state("finished")
                .status("SUCCESS")
                .build();

        WireMock.setupServer(post(BUILD_QUEUE.getUrl()), HttpStatus.SC_OK, fakeBuild);
        WireMock.setupServer(get(urlPathMatching(BUILD_QUEUE.getUrl() + "/id%3A\\d+")), HttpStatus.SC_OK, fakeBuild);
    }

    @BeforeMethod
    public void setupBuildData() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
    }

    public Build generateBuild() {
        SampleBuildGenerator sampleBuildGenerator = new SampleBuildGenerator(userCheckRequests);
        return sampleBuildGenerator.createSampleBuild(testData, true);
    }

    public void checkBuildResults(Build buildResult) {
        softy.assertEquals(buildResult.getState(), "finished");
        softy.assertEquals(buildResult.getStatus(), "SUCCESS");
    }

    @Test(description = "User should be able to start build (with WireMock)",
            groups = {"Regression"})
    public void userStartsBuildWithWireMockTest() {
        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);

        Build buildResult = checkedBuildQueueRequest.create(Build.builder()
                .buildType(testData.getBuildType())
                .build());
        checkBuildResults(buildResult);
    }

    @Test(description = "User should be able to start build (without WireMock) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldTest() {
        Build build = generateBuild();
        waitUntilBuildFinished(userCheckRequests, build.getId());
        Build buildResult = (Build) userCheckRequests.getRequest(BUILD_QUEUE).read("id:" + build.getId());
        checkBuildResults(buildResult);
    }


    @Test(description = "User should be able to start build (with WireMock) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldWireMockTest() {
        Build build = generateBuild();
        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);

        Build buildResult = checkedBuildQueueRequest.read("id:" + build.getId());
        checkBuildResults(buildResult);
    }

    @AfterClass(alwaysRun = true)
    public void stopWireMockServer() {
        WireMock.stopServer();
    }
}