package com.example.teamcity.api.errors;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuildErrorMessages {
    BUILD_CONFIG_IN_USE("The build configuration / template ID \"%s\" is already used by another configuration or template"),
    NO_PERMISSIONS("You do not have enough permissions to edit project with id: %s");

    private final String error;
}
