package com.example.teamcity.api.generators;

import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import lombok.AllArgsConstructor;

import java.util.Collections;

import static com.example.teamcity.api.enums.Endpoint.*;

@AllArgsConstructor
public final class SampleBuildGenerator {
    private CheckedRequests userCheckRequests;

    public Build createSampleBuild(TestData testData, Boolean isReadyToRun) {
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        var buildType = testData.getBuildType();

        Steps steps = Steps.builder()
                .step(Collections.singletonList(StepGenerator.generateSampleScript()))
                .build();
        buildType.setSteps(steps);

        BuildType buildTypeResp = (BuildType) userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        var buildTypeTempl = BuildType.builder()
                .id(buildTypeResp.getId())
                .build();

        if (isReadyToRun) {
            var buildToRun = Build.builder()
                    .buildType(buildTypeTempl)
                    .build();
            return (Build) userCheckRequests.getRequest(BUILD_QUEUE).create(buildToRun);
        } else {
            return null;
        }
    }
}
