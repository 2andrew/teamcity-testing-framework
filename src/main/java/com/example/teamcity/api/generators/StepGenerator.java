package com.example.teamcity.api.generators;

import com.example.teamcity.api.enums.BuilderTypes;
import com.example.teamcity.api.models.Property;
import com.example.teamcity.api.models.PropertyContainer;
import com.example.teamcity.api.models.Step;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class StepGenerator {
    private static PropertyContainer buildPropertyContainer(Map<String, String> propertiesMap) {
        List<Property> properties = propertiesMap.entrySet().stream()
                .map(entry -> Property.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return PropertyContainer.builder()
                .propertyList(properties)
                .build();
    }

    public static Step generateSampleScript() {
        Map<String, String> propertiesMap = Map.of(
                "use.custom.script", "true",
                "script.content", "echo Hello World"
        );
        return Step.builder()
                .type(BuilderTypes.SIMPLE_RUNNER.getBuilderType())
                .name("hello world")
                .propertyContainer(buildPropertyContainer(propertiesMap))
                .build();
    }
}
