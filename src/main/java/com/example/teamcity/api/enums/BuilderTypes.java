package com.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuilderTypes {
    SIMPLE_RUNNER("simpleRunner");

    private final String builderType;
}
