package com.example.teamcity.api.models;

import com.example.teamcity.api.annotations.Optional;
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
public class Property extends BaseModel{
    @Optional
    private String name;
    @Optional
    private String value;
}