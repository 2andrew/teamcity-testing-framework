package com.example.teamcity.api.dataproviders;

import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.models.Project;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class ProjectDataProviders {
    private static final int ID_MAX_LENGTH = 225;
    private static final String ID_COMMON_RULE = "ID should start with a latin letter and contain only latin letters, digits and underscores (at most %d characters).";
    private static final String ID_STARTS_RULE = "Project ID \"%s\" is invalid: starts with non-letter character ";

    private static final int NAME_MAX_LENGTH_UI = 80;

    @DataProvider(name = "validProjects")
    public static Object[][] validProjects() {
        return new Object[][]{
                {generate(Project.class)},
                {generate(Project.class, RandomData.getRandomCharacter(), RandomData.getRandomCharacter())}, // min length
                {generate(Project.class, RandomData.getString(ID_MAX_LENGTH), RandomData.getString(NAME_MAX_LENGTH_UI * 2000))}, // name is not limited in API?
                {generate(Project.class, RandomData.getString(), RandomData.getUnderscoreString())},
                {generate(Project.class, RandomData.getString(), RandomData.getRandomNumber() + RandomData.getString())},
                {generate(Project.class, RandomData.getString() + RandomData.getUnderscoreString() + RandomData.getAlphaNumericString())},
        };
    }

    @DataProvider(name = "invalidProjects")
    public static Object[][] invalidProjects() {
        Project project1_1 = generate(Project.class, "", RandomData.getString());
        Project project1_2 = generate(Project.class, "              ", RandomData.getString()); //spaces+tab
        Project project2 = generate(Project.class, RandomData.getString(ID_MAX_LENGTH + 1), RandomData.getString());
        Project project3 = generate(Project.class, RandomData.getEmoji() + RandomData.getString(), RandomData.getString());
        Project project4 = generate(Project.class, RandomData.getRandomNumber() + RandomData.getString(), RandomData.getString());
        Project project5 = generate(Project.class, RandomData.getUnderscoreString(), RandomData.getString());
        Project project6_1 = generate(Project.class, RandomData.getString(), "");
        Project project6_2 = generate(Project.class, RandomData.getString(), "              "); //spaces+tab

        return new Object[][]{
                // weird that empty ID and Name produce different status codes in different cases
                {project1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Project ID must not be empty."},
                {project1_2, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Project ID must not be empty."},
                {project6_1, HttpStatus.SC_BAD_REQUEST, "Project name cannot be empty."},
                {project6_2, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Given project name is empty."},
                {project2, HttpStatus.SC_INTERNAL_SERVER_ERROR, ("Project ID \"%s\" is invalid: it is %d characters long while the maximum length is %d. " + ID_COMMON_RULE).formatted(project2.getId(), ID_MAX_LENGTH + 1, ID_MAX_LENGTH, ID_MAX_LENGTH)},
                {project3, HttpStatus.SC_INTERNAL_SERVER_ERROR, (ID_STARTS_RULE + "'?'. " + ID_COMMON_RULE).formatted(project3.getId(), ID_MAX_LENGTH)},
                {project4, HttpStatus.SC_INTERNAL_SERVER_ERROR, (ID_STARTS_RULE + "'%s'. " + ID_COMMON_RULE).formatted(project4.getId(), project4.getId().substring(0, 1), ID_MAX_LENGTH)},
                {project5, HttpStatus.SC_INTERNAL_SERVER_ERROR, (ID_STARTS_RULE + "'_'. " + ID_COMMON_RULE).formatted(project5.getId(), ID_MAX_LENGTH)},
        };
    }
}
