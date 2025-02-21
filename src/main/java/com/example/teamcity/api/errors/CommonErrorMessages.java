package com.example.teamcity.api.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorMessages {

    AUTH_REQUIRED("Authentication required");

    private final String error;
}
