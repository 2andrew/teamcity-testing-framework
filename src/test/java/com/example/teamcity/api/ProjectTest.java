package com.example.teamcity.api;

import com.example.teamcity.api.dataproviders.ProjectDataProviders;
import com.example.teamcity.api.enums.AvailableScopes;
import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidationResponseSpecifications;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Collections;

import static com.example.teamcity.api.enums.Endpoint.PROJECTS;
import static com.example.teamcity.api.enums.Endpoint.USERS;
import static com.example.teamcity.api.errors.ProjectErrorMessages.*;
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

        Project createdProject = userCheckRequests.<Project>getRequest(PROJECTS).create(project);

        softy.assertEquals(createdProject, project, "Invalid project created");
    }

    @Test(description = "User should not be able to create two projects with the same id", groups = {"Negative", "Uniqueness", "CRUD"})
    public void userCreatesTwoProjectsWithTheSameIdTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var projectWithSameId = generate(Project.class, testData.getProject().getId());

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithSameId)
                .then().spec(ValidationResponseSpecifications.checkIdNameInUse(ID_IN_USE.getError().formatted(projectWithSameId.getId())));
    }

    @Test(description = "User should not be able to create two projects with the same name", groups = {"Negative", "Uniqueness", "CRUD"})
    public void userCreatesTwoProjectsWithTheSameNameTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var projectWithSameName = generate(Project.class, RandomData.getString(), testData.getProject().getName());

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithSameName)
                .then().spec(ValidationResponseSpecifications.checkIdNameInUse(NAME_IN_USE.getError().formatted(projectWithSameName.getName())));
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

    @Test(description = "User should not be able to create project with invalid roles",
            dataProvider = "invalidRoles",
            dataProviderClass = ProjectDataProviders.class,
            groups = {"Negative", "Roles", "CRUD"})
    public void userCreatesProjectWithInvalidRolesTest(String roleName) {
        var user = testData.getUser();
        user.getRoles().setRole(Collections.singletonList(
                new Role(roleName, AvailableScopes.GLOBAL.getScope())));

        superUserCheckRequests.getRequest(USERS).create(user);

        new UncheckedBase(Specifications.authSpec(user), PROJECTS)
                .create(testData.getProject())
                .then().spec(ValidationResponseSpecifications.checkNoPermissions(NO_PERMISSION_CREATE.getError().formatted(testData.getProject().getId())));
    }

    @Test(description = "Unathorized user should not be able to create project", groups = {"Negative", "Security", "CRUD"})
    public void unathorizedUserShouldntBeAbleCreateProjectTest() {
        new UncheckedBase(Specifications.unauthSpec(), PROJECTS)
                .create(generate(Project.class))
                .then().spec(ValidationResponseSpecifications.checkUnAuthorized());
    }
}