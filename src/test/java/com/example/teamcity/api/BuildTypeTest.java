package com.example.teamcity.api;

import com.example.teamcity.api.enums.AvailableRoles;
import com.example.teamcity.api.enums.AvailableScopes;
import com.example.teamcity.api.models.BuildType;
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
import static com.example.teamcity.api.errors.BuildErrorMessages.BUILD_CONFIG_IN_USE;
import static com.example.teamcity.api.errors.BuildErrorMessages.NO_PERMISSIONS;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;


@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Collections.singletonList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().spec(ValidationResponseSpecifications.checkIdNameInUse(BUILD_CONFIG_IN_USE.getError().formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        var projectAdmin = testData.getUser();
        projectAdmin.getRoles().setRole(Collections.singletonList(
                new Role(AvailableRoles.PROJECT_ADMIN.getRoleName(), AvailableScopes.PROJECT.getScope().formatted(testData.getProject().getId()))));
        superUserCheckRequests.getRequest(USERS).create(projectAdmin);

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(projectAdmin));
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES)
                .read(testData.getBuildType().getId());
        createdBuildType.setSteps(null); // not verifying them in this test because it's out of scope

        softy.assertEquals(createdBuildType, testData.getBuildType());
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        // testData contains project and buildType for SUPER_ADMIN
        var superAdminProject = testData.getProject();
        superUserCheckRequests.getRequest(PROJECTS).create(superAdminProject);

        // creating PROJECT_ADMIN with access to superAdminProject
        var projectAdmin = testData.getUser();
        projectAdmin.getRoles().setRole(Collections.singletonList(
                new Role(AvailableRoles.PROJECT_ADMIN.getRoleName(), AvailableScopes.PROJECT.getScope().formatted(superAdminProject.getId()))));
        superUserCheckRequests.getRequest(USERS).create(projectAdmin);

        // Creating newProject as SUPER_ADMIN
        var newProject = generate(Project.class);
        superUserCheckRequests.getRequest(PROJECTS).create(newProject);

        // Creating buildType as PROJECT_ADMIN for SUPER_ADMIN's project called newProject
        var buildTypeForNewProject = generate(BuildType.class);
        buildTypeForNewProject.setProject(newProject);

        new UncheckedRequests(Specifications.authSpec(projectAdmin))
                .getRequest(BUILD_TYPES)
                .create(buildTypeForNewProject)
                .then().spec(ValidationResponseSpecifications.checkNoPermissions(NO_PERMISSIONS.getError().formatted(newProject.getId())));
    }
}