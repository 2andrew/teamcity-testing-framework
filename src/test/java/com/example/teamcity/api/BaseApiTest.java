package com.example.teamcity.api;

import com.example.teamcity.BaseTest;
import com.example.teamcity.api.generators.SampleBuildGenerator;
import com.example.teamcity.api.models.AuthModules;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.ServerAuthSettings;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.ServerAuthRequest;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class BaseApiTest extends BaseTest {
    private final ServerAuthRequest serverAuthRequest = new ServerAuthRequest(Specifications.superUserSpec());
    private AuthModules authModules;
    private boolean perProjectPermissions;

    @BeforeSuite(alwaysRun = true)
    public void setUpServerAuthSettings() {
        perProjectPermissions = serverAuthRequest.read().getPerProjectPermissions();

        authModules = generate(AuthModules.class);
        serverAuthRequest.update(ServerAuthSettings.builder()
                .perProjectPermissions(true)
                .modules(authModules)
                .build());
    }

    @AfterSuite(alwaysRun = true)
    public void cleanUpServerAuthSettings() {
        serverAuthRequest.update(ServerAuthSettings.builder()
                .perProjectPermissions(perProjectPermissions)
                .modules(authModules)
                .build());
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
}
