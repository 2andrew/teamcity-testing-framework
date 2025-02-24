package com.example.teamcity.api.dataproviders;

import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.models.Project;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.stream.IntStream;

import static com.example.teamcity.api.enums.AvailableRoles.*;
import static com.example.teamcity.api.errors.ProjectErrorMessages.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class ProjectDataProviders {
    private static final int ID_MAX_LENGTH = 225;
    private static final int NAME_MAX_LENGTH = 80;

    @DataProvider(name = "validProjects")
    public static Object[][] validProjects() {
        return new Object[][]{
                {generate(Project.class)},
                {generate(Project.class, RandomData.getRandomCharacter(), RandomData.getRandomCharacter())},
                {generate(Project.class, RandomData.getString(ID_MAX_LENGTH), RandomData.getString(NAME_MAX_LENGTH * 2000))}, // name is not limited in API?
                {generate(Project.class, RandomData.getString(), RandomData.getUnderscoreString())},
                {generate(Project.class, RandomData.getString(), RandomData.getRandomNumber() + RandomData.getString())},
                {generate(Project.class, RandomData.getString() + RandomData.getUnderscoreString() + RandomData.getAlphaNumericString())},
                {generate(Project.class, RandomData.getString(), RandomData.getCyrillicString())},
        };
    }

    @DataProvider(name = "invalidProjects")
    public static Object[][] invalidProjects() {
        List<Project> projects = List.of(
                generate(Project.class, "", RandomData.getString()),
                generate(Project.class, "              ", RandomData.getString()),
                generate(Project.class, "", ""),
                generate(Project.class, RandomData.getUnderscoreString(), ""),
                generate(Project.class, RandomData.getString(), ""),
                generate(Project.class, RandomData.getString(), "              "),
                generate(Project.class, RandomData.getString(ID_MAX_LENGTH + 1), RandomData.getString()),
                generate(Project.class, RandomData.getEmoji() + RandomData.getString(), RandomData.getString()),
                generate(Project.class, RandomData.getRandomNumber() + RandomData.getString(), RandomData.getString()),
                generate(Project.class, RandomData.getUnderscoreString(), RandomData.getString()),
                generate(Project.class, RandomData.getCyrillicString(), RandomData.getString()),
                generate(Project.class, RandomData.getSpecialCharacterString(), RandomData.getString()));

        List<Integer> statuses = List.of(
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_BAD_REQUEST,
                HttpStatus.SC_BAD_REQUEST,
                HttpStatus.SC_BAD_REQUEST,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_INTERNAL_SERVER_ERROR,
                HttpStatus.SC_INTERNAL_SERVER_ERROR
        );

        List<String> errors = List.of(
                EMPTY_ID.getError(),
                EMPTY_ID.getError(),
                EMPTY_NAME.getError(),
                EMPTY_NAME.getError(),
                EMPTY_NAME.getError(),
                EMPTY_GIVEN_NAME.getError(),
                INVALID_CHARACTER_ID.getError().formatted(projects.get(6).getId(), ID_MAX_LENGTH + 1, ID_MAX_LENGTH, ID_MAX_LENGTH),
                INVALID_NON_LETTER_STARTS_ID.getError().formatted(projects.get(7).getId(), "?", ID_MAX_LENGTH),
                INVALID_NON_LETTER_STARTS_ID.getError().formatted(projects.get(8).getId(), projects.get(8).getId().substring(0, 1), ID_MAX_LENGTH),
                INVALID_NON_LETTER_STARTS_ID.getError().formatted(projects.get(9).getId(), "_", ID_MAX_LENGTH),
                INVALID_NON_LETTER_CONTAINS_ID.getError().formatted(projects.get(10).getId(), projects.get(10).getId().substring(0, 1), ID_MAX_LENGTH),
                INVALID_NON_LETTER_STARTS_ID.getError().formatted(projects.get(11).getId(), projects.get(11).getId().substring(0, 1), ID_MAX_LENGTH)
        );

        return IntStream.range(0, projects.size())
                .mapToObj(i -> new Object[]{projects.get(i), statuses.get(i), errors.get(i)})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "invalidRoles")
    public static Object[][] roles() {
        return new Object[][]{
                {PROJECT_VIEWER.getRoleName()},
                {PROJECT_DEVELOPER.getRoleName()},
                {AGENT_MANAGER.getRoleName()},
        };
    }

}
