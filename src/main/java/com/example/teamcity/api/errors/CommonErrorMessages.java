package com.example.teamcity.api.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorMessages {

    AUTH_REQUIRED("Authentication required"),
    NO_ADMIN_ACCOUNT_ON_SERVER("There is no administrator account on the server");

    private final String error;
}
