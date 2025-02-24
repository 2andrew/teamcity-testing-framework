package com.example.teamcity.api;

import com.example.teamcity.api.enums.AvailableRoles;
import com.example.teamcity.api.enums.AvailableScopes;
import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidationResponseSpecifications;
import org.testng.annotations.Test;

import java.util.Collections;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.errors.ProjectErrorMessages.INVALID_PROJECT_FOUND;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;


@Test(groups = {"Regression"})
public class ProjectSearchTest extends BaseApiTest {

    @Test(description = "User should be able to search project by Name",
            groups = {"Positive", "Search"})
    public void userShouldBeAbleSearchProjectByNameTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var result = userCheckRequests.<Project>getRequest(PROJECTS).search("name:%s".formatted(testData.getProject().getName()));

        softy.assertEquals(result, createdProject, INVALID_PROJECT_FOUND.getError());
    }

    @Test(description = "Project admin can find project created by Super Admin ", groups = {"Positive", "Roles"})
    public void projectAdminSearchesProjectWithoutAccessTest() {
        var superAdminProject = testData.getProject();
        superUserCheckRequests.getRequest(PROJECTS).create(superAdminProject);

        var projectAdmin = testData.getUser();
        projectAdmin.getRoles().setRole(Collections.singletonList(
                new Role(AvailableRoles.PROJECT_ADMIN.getRoleName(), AvailableScopes.PROJECT.getScope().formatted(superAdminProject.getId()))));
        superUserCheckRequests.getRequest(USERS).create(projectAdmin);

        var newProject = generate(Project.class);
        superUserCheckRequests.getRequest(PROJECTS).create(newProject);

        var userCheckRequests = new UncheckedRequests(Specifications.authSpec(projectAdmin));

        var result = userCheckRequests.getRequest(PROJECTS).search("name:%s".formatted(newProject.getName()));

        softy.assertEquals(result.as(Project.class), newProject, INVALID_PROJECT_FOUND.getError());
    }

    @Test(description = "User should not be able to search project by non-existent Name",
            groups = {"Negative", "Search"})
    public void userShouldNotBeAbleSearchProjectByNonExistentNameTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var search_name = RandomData.getAlphaNumericString();

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .search("name:%s".formatted(search_name))
                .then().spec(ValidationResponseSpecifications.checkProjectNotFound(search_name));
    }

}
