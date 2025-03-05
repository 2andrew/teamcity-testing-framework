package com.example.teamcity.ui.errors;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuildTypeErrorMessagesUI {
    ALREADY_EXISTS("Build configuration with name \"%s\" already exists in project: \"%s\"");

    private final String error;
}
