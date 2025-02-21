package com.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AvailableRoles {
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    PROJECT_ADMIN("PROJECT_ADMIN"),
    PROJECT_VIEWER("PROJECT_VIEWER"),
    PROJECT_DEVELOPER("PROJECT_DEVELOPER"),
    AGENT_MANAGER("AGENT_MANAGER");

    private final String roleName;
}
