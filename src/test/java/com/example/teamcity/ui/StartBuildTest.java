package com.example.teamcity.ui;

import com.example.teamcity.api.generators.SampleBuildGenerator;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.ProjectsBuildsPage;
import org.testng.annotations.Test;

import static com.example.teamcity.api.custom.AsyncConditions.waitUntilBuildFinished;

@Test(groups = {"Regression"})
public class StartBuildTest extends BaseUiTest {

    @Test(description = "User should be able to start build", groups = {"Positive"})
    public void userCreatesProject() {
        loginAs(testData.getUser());

        CheckedRequests userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        SampleBuildGenerator sampleBuildGenerator = new SampleBuildGenerator(userCheckRequests);
        sampleBuildGenerator.createSampleBuild(testData, false);

        ProjectsBuildsPage projectsBuildsPage = ProjectsBuildsPage.open(testData.getProject().getId());
        projectsBuildsPage.runBuild(testData.getBuildType().getName());

        waitUntilBuildFinished(userCheckRequests, testData.getBuildType());

        var buildResult = projectsBuildsPage.getBuildDetailsElement(1);
        softy.assertEquals(buildResult.getBuild_number().getText(), "#1");
        softy.assertEquals(buildResult.getBuild_status().getText(), "Success");
    }
}
