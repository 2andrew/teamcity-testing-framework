package com.example.teamcity.ui;

import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.elements.SidebarRow;
import com.example.teamcity.ui.errors.ProjectErrorMessagesUI;
import com.example.teamcity.ui.pages.ProjectsPage;
import org.testng.annotations.Test;

import java.util.List;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;

@Test(groups = {"Regression"})
public class SearchProjectTest extends BaseUiTest {


    @Test(description = "User should be able to search project by name", groups = {"Positive"})
    public void userSearchProject() {
        loginAs(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        ProjectsPage projectsPage = ProjectsPage.open();
        projectsPage.searchProjects(testData.getProject().getName());

        List<SidebarRow> rows = projectsPage.getSidebarRows();

        payloadValidator.validateSize(rows.size(), 1);
        payloadValidator.validateAttribute(rows.getFirst().getName().text(), createdProject.getName());
    }

    @Test(description = "User should not be able to search project that doesn't exist", groups = {"Negative"})
    public void userSearchProjectNotExist() {
        loginAs(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        ProjectsPage projectsPage = ProjectsPage.open();
        projectsPage.searchProjects(RandomData.getString(50));

        List<SidebarRow> rows = projectsPage.getSidebarRows();

        payloadValidator.validateSize(rows.size(), 0);
        payloadValidator.validateAttribute(projectsPage.placeholderText.text(), ProjectErrorMessagesUI.NOTHING_FOUND.getError());
    }
}
