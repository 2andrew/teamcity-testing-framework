package com.example.teamcity.api.errors;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectErrorMessages {
    EMPTY_ID("Project ID must not be empty."),
    EMPTY_NAME("Project name cannot be empty."),
    EMPTY_GIVEN_NAME("Given project name is empty."),

    INVALID_CHARACTER_ID("Project ID \"%s\" is invalid: it is %d characters long while the maximum length is %d. ID should start with a latin letter and contain only latin letters, digits and underscores (at most %d characters)."),
    INVALID_NON_LETTER_STARTS_ID("Project ID \"%s\" is invalid: starts with non-letter character '%s'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most %d characters)."),
    INVALID_NON_LETTER_CONTAINS_ID("Project ID \"%s\" is invalid: contains non-latin letter '%s'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most %d characters)."),

    ID_IN_USE("Project ID \"%s\" is already used by another project"),
    NAME_IN_USE("Project with this name already exists: %s"),

    NO_PERMISSION_CREATE("You do not have \"Create subproject\" permission in project"),

    INVALID_PROJECT_FOUND("Invalid project found"),
    NO_PROJECT_FOUND("No project found by name or internal/external id '%s'");

    private final String error;
}
