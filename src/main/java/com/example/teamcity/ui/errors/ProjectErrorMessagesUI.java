package com.example.teamcity.ui.errors;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectErrorMessagesUI {
    EMPTY_NAME("Project name must not be empty"),
    NOTHING_FOUND("Nothing found");

    private final String error;
}
