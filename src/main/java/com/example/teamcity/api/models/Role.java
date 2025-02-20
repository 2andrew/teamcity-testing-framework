package com.example.teamcity.api.models;

import com.example.teamcity.api.enums.AvailableRoles;
import com.example.teamcity.api.enums.AvailableScopes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role extends BaseModel {
    @Builder.Default
    private String roleId = AvailableRoles.SYSTEM_ADMIN.getRoleName();
    @Builder.Default
    private String scope = AvailableScopes.GLOBAL.getScope();
}