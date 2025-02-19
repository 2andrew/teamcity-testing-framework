package com.example.teamcity.api;

import com.example.teamcity.api.dataproviders.ProjectDataProviders;
import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;


@Test(groups = {"Regression"})
public class ProjectTest extends BaseApiTest {

    @Test(description = "User should be able to create project",
            dataProvider = "validProjects",
            dataProviderClass = ProjectDataProviders.class,
            groups = {"Positive", "Boundary", "CRUD"})
    public void userCreateProjectTest(Project project) {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(project);
    }

    @Test(description = "User should not be able to create two projects with the same id", groups = {"Negative", "Uniqueness", "CRUD"})
    public void userCreatesTwoProjectsWithTheSameIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var projectWithSameId = generate(Project.class, testData.getProject().getId());

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project ID \"%s\" is already used by another project".formatted(testData.getProject().getId())));
    }

    @Test(description = "User should not be able to create two projects with the same name", groups = {"Negative", "Uniqueness", "CRUD"})
    public void userCreatesTwoProjectsWithTheSameNameTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var projectWithSameName = generate(Project.class, RandomData.getString(), testData.getProject().getName());

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithSameName)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project with this name already exists: %s".formatted(testData.getProject().getName())));
    }

    @Test(description = "User should not be able to create project with invalid data",
            dataProvider = "invalidProjects",
            dataProviderClass = ProjectDataProviders.class,
            groups = {"Negative", "Boundary", "CRUD"})
    public void userShouldNotCreateProjectWithInvalidDataTest(Project project, int statusCode, String expectedErrorMessage) {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(project)
                .then().assertThat().statusCode(statusCode)
                .body(Matchers.containsString(expectedErrorMessage));
    }
}