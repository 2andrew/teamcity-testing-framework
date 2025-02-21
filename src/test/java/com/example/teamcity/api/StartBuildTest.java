package com.example.teamcity.api;

import com.example.teamcity.api.models.*;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {
    @BeforeMethod
    public void setupWireMockServer() {
        var fakeBuild = Build.builder()
                .state("finished")
                .status("SUCCESS")
                .build();

        WireMock.setupServer(post(BUILD_QUEUE.getUrl()), HttpStatus.SC_OK, fakeBuild);
        WireMock.setupServer(get(urlPathMatching(BUILD_QUEUE.getUrl() + "/id%3A\\d+")), HttpStatus.SC_OK, fakeBuild);
    }

    private static boolean isBuildFinished(CheckedRequests req, String buildId) {
        Build build = (Build) req.getRequest(BUILD_QUEUE).read(buildId);
        return build.getState().equals("finished");
    }

    @Test(description = "User should be able to start build (with WireMock)",
            groups = {"Regression"})
    public void userStartsBuildWithWireMockTest() {
        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);

        var build = checkedBuildQueueRequest.create(Build.builder()
                .buildType(testData.getBuildType())
                .build());

        softy.assertEquals(build.getState(), "finished");
        softy.assertEquals(build.getStatus(), "SUCCESS");
    }

    @Test(description = "User should be able to start build (without WireMock) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        var buildType = testData.getBuildType();

        Property propertyUse = Property.builder()
                .name("use.custom.script")
                .value("true")
                .build();
        Property propertyContent = Property.builder()
                .name("script.content")
                .value("echo Hello World")
                .build();
        List<Property> properties = Arrays.asList(propertyUse, propertyContent);
        PropertyContainer propertyContainer = PropertyContainer.builder()
                .propertyList(properties)
                .build();
        Step helloWorldStep = Step.builder()
                .type("simpleRunner")
                .name("hello world")
                .propertyContainer(propertyContainer)
                .build();
        Steps steps = Steps.builder()
                .step(Collections.singletonList(helloWorldStep))
                .build();
        buildType.setSteps(steps);

        BuildType buildTypeResp = (BuildType) userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var buildtypeTempl = BuildType.builder()
                .id(buildTypeResp.getId())
                .build();

        var buildToRun = Build.builder()
                .buildType(buildtypeTempl)
                .build();
        Build build = (Build) userCheckRequests.getRequest(BUILD_QUEUE).create(buildToRun);

        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> isBuildFinished(userCheckRequests, build.getId()));

        var buildNew = (Build) userCheckRequests.getRequest(BUILD_QUEUE).read(build.getId());

        softy.assertEquals(buildNew.getState(), "finished");
        softy.assertEquals(buildNew.getStatus(), "SUCCESS");
    }


    @Test(description = "User should be able to start build (with WireMock) and run echo 'Hello, world!'",
            groups = {"Regression"})
    public void userStartsBuildWithHelloWorldWireMockTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        var buildType = testData.getBuildType();

        Property propertyUse = Property.builder()
                .name("use.custom.script")
                .value("true")
                .build();
        Property propertyContent = Property.builder()
                .name("script.content")
                .value("echo Hello World")
                .build();
        List<Property> properties = Arrays.asList(propertyUse, propertyContent);
        PropertyContainer propertyContainer = PropertyContainer.builder()
                .propertyList(properties)
                .build();
        Step helloWorldStep = Step.builder()
                .type("simpleRunner")
                .name("hello world")
                .propertyContainer(propertyContainer)
                .build();
        Steps steps = Steps.builder()
                .step(Collections.singletonList(helloWorldStep))
                .build();
        buildType.setSteps(steps);

        BuildType buildTypeResp = (BuildType) userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var buildtypeTempl = BuildType.builder()
                .id(buildTypeResp.getId())
                .build();

        var buildToRun = Build.builder()
                .buildType(buildtypeTempl)
                .build();
        Build build = (Build) userCheckRequests.getRequest(BUILD_QUEUE).create(buildToRun);

        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications.mockSpec(), BUILD_QUEUE);
        Build buildNew = checkedBuildQueueRequest.read(build.getId());

        softy.assertEquals(buildNew.getState(), "finished");
        softy.assertEquals(buildNew.getStatus(), "SUCCESS");
    }


    @AfterMethod(alwaysRun = true)
    public void stopWireMockServer() {
        WireMock.stopServer();
    }
}