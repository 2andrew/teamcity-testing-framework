package com.example.teamcity.ui;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.errors.BuildTypeErrorMessagesUI;
import com.example.teamcity.ui.pages.ProjectsBuildsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildConfigPage;
import org.testng.annotations.Test;

import java.util.Optional;

import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.example.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = {"Regression"})
public class CreateBuildTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";
    private static final String ATTR_NAME = "buildType";


    @Test(description = "User should be able to create build", groups = {"Positive"})
    public void userCreatesBuild() {
        loginAs(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        CreateBuildConfigPage.open(createdProject.getId())
                .createForm(REPO_URL)
                .setupBuild(testData.getBuildType().getName());

        Optional<BuildType> createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).findAll(ATTR_NAME)
                .stream()
                .filter(item -> item.getName().equals(testData.getBuildType().getName()))
                .findFirst();

        payloadValidator.assertEqualsIfPresent(createdBuildType, testData.getBuildType(),
                "Build type name is not correct", BuildType::getName);

        var foundBuilds = ProjectsBuildsPage.open(testData.getProject().getId())
                .getBuildConfigElements().stream()
                .anyMatch(build -> build.getName().text().equals(testData.getBuildType().getName()));

        softy.assertTrue(foundBuilds);
    }


    @Test(description = "User should not be able to create build with name that already exist", groups = {"Positive"})
    public void userCreatesBuildDuplicatedNane() {
        loginAs(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        testData.getBuildType().setProject(createdProject);
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        CreateBuildConfigPage createdBuildConfigPage = CreateBuildConfigPage.open(createdProject.getId());
        createdBuildConfigPage
                .createForm(REPO_URL)
                .setupBuild(testData.getBuildType().getName());

        String errorMessage = createdBuildConfigPage.getBuildTypeNameInputErrorText();

        payloadValidator.validateAttribute(errorMessage, BuildTypeErrorMessagesUI.ALREADY_EXISTS.getError().
                formatted(testData.getBuildType().getName(), testData.getProject().getName()));

        var createdBuildTypes = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).findAll(ATTR_NAME)
                .stream()
                .filter(item -> item.getName().equals(testData.getBuildType().getName()))
                .toList();

        payloadValidator.validateSize(createdBuildTypes.size(), 1);
    }
}
