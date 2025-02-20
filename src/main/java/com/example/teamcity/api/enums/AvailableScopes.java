package com.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AvailableScopes {
    GLOBAL("g"),
    PROJECT("p:%s");

    private final String scope;
}
