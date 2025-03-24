package com.example.teamcity.api;

import com.example.teamcity.api.generators.SampleBuildGenerator;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.common.MockServerInstance;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Feature;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.example.teamcity.api.custom.AsyncConditions.waitUntilBuildFinished;
import static com.example.teamcity.api.enums.Endpoint.BUILD_QUEUE;
import static com.example.teamcity.api.enums.Endpoint.USERS;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {

    static class CustomDispatcher extends Dispatcher {
        @SneakyThrows
        @Override
        public MockResponse dispatch(RecordedRequest request) {
            var fakeBuild = Build.builder()
                    .state("finished")
                    .status("SUCCESS")
                    .build();
            String jsonResponse = new ObjectMapper().writeValueAsString(fakeBuild);

            String path = request.getPath();
            if (path.equals(BUILD_QUEUE.getUrl())) {
                return new MockResponse()
                        .setResponseCode(HttpStatus.SC_OK)
                        .addHeader("Content-Type", "application/json")
                        .setBody(jsonResponse);
            } else if (path.matches(BUILD_QUEUE.getUrl() + "/id%3A\\d+")) {
                return new MockResponse()
                        .setResponseCode(HttpStatus.SC_OK)
                        .addHeader("Content-Type", "application/json")
                        .setBody(jsonResponse);
            }
            return new MockResponse().setResponseCode(HttpStatus.SC_NOT_FOUND);
        }
    }

    @BeforeClass
    public void setupMockServer() {
        MockServerInstance.startServer(new CustomDispatcher());
    }

    public CheckedRequests setupBuildData() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        return new CheckedRequests(Specifications.authSpec(testData.getUser()));
    }

    public Build generateBuild(CheckedRequests userCheckRequests) {
        SampleBuildGenerator sampleBuildGenerator = new SampleBuildGenerator(userCheckRequests);
        return sampleBuildGenerator.createSampleBuild(testData, true);
    }

    public void checkBuildResults(Build buildResult) {
        softy.assertEquals(buildResult.getState(), "finished");
        softy.assertEquals(buildResult.getStatus(), "SUCCESS");
    }

    @Test(description = "User should be able to start build (with MockServer)",
            groups = {"Regression"}, enabled = false)
    public void userStartsBuildWithMockServerTest() {
        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);

        Build buildResult = checkedBuildQueueRequest.create(Build.builder()
                .buildType(testData.getBuildType())
                .build());
        checkBuildResults(buildResult);
    }

    @Test(description = "User should be able to start build (without MockServer) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldTest() {
        CheckedRequests userCheckRequests = setupBuildData();
        Build build = generateBuild(userCheckRequests);

        waitUntilBuildFinished(userCheckRequests, build.getId());

        Build buildResult = (Build) userCheckRequests.getRequest(BUILD_QUEUE).read("id:" + build.getId());
        checkBuildResults(buildResult);
    }


    @Test(description = "User should be able to start build (with MockServer) and run echo 'Hello, world!'",
            groups = {"Regression"}, enabled = false)
    public void userStartsBuildWithHelloWorldMockServerTest() {
        CheckedRequests userCheckRequests = setupBuildData();
        Build build = generateBuild(userCheckRequests);

        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);

        Build buildResult = checkedBuildQueueRequest.read("id:" + build.getId());
        checkBuildResults(buildResult);
    }

    @AfterClass(alwaysRun = true)
    public void stopMockServerServer() {
        MockServerInstance.stopServer();
    }
}