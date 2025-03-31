package com.example.teamcity.api;

import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.common.WireMockInstance;
import io.qameta.allure.Feature;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.BUILD_QUEUE;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Feature("Start build")
public class StartBuildWiremockTest extends BaseApiTest {

    @BeforeClass(alwaysRun = true)
    public void setupMockServer() {
        var fakeBuild = Build.builder()
                .state("finished")
                .status("SUCCESS")
                .build();
        WireMockInstance.setupServer(post(BUILD_QUEUE.getUrl()), HttpStatus.SC_OK, fakeBuild);
        WireMockInstance.setupServer(get(urlPathMatching(BUILD_QUEUE.getUrl() + "/id%3A\\d+")), HttpStatus.SC_OK, fakeBuild);
    }

    @Test(description = "User should be able to start build (with MockServer)",
            groups = {"Regression"})
    public void userStartsBuildWithMockServerTest() {
        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);

        Build buildResult = checkedBuildQueueRequest.create(Build.builder()
                .buildType(testData.getBuildType())
                .build());
        checkBuildResults(buildResult);
    }


    @Test(description = "User should be able to start build (with MockServer) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldMockServerTest() {
        CheckedRequests userCheckRequests = setupBuildData();
        Build build = generateBuild(userCheckRequests);

        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);

        Build buildResult = checkedBuildQueueRequest.read("id:" + build.getId());
        checkBuildResults(buildResult);
    }

    @AfterClass(alwaysRun = true)
    public void stopWireMockServer() {
        WireMockInstance.stopServer();
    }
}