package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.errors.ProjectErrorMessagesUI;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import org.testng.annotations.Test;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";
    private static final String ATTR_NAME = "project";


    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreatesProject() {
        loginAs(testData.getUser());

        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        softy.assertNotNull(createdProject);

        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var foundProjects = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().text().equals(testData.getProject().getName()));

        softy.assertTrue(foundProjects);
    }

    @Test(description = "User should not be able to create project without name", groups = {"Negative"})
    public void userCreatesProjectWithoutName() {
        loginAs(testData.getUser());

        var projectsBefore = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).findAll(ATTR_NAME);

        CreateProjectPage create_project_page = CreateProjectPage.open("_Root")
                .createForm(REPO_URL);
        create_project_page.setupProject("", testData.getBuildType().getName());

        var projectsAfter = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).findAll(ATTR_NAME);
        payloadValidator.validateSize(projectsAfter.size(), projectsBefore.size()); //might fail during parallel running

        String errorMessage = create_project_page.getProjectNameInputError().text();
        payloadValidator.validateAttribute(errorMessage, ProjectErrorMessagesUI.EMPTY_NAME.getError());
    }
}
