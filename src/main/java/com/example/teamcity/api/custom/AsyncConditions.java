package com.example.teamcity.api.custom;

import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.requests.CheckedRequests;
import org.awaitility.Awaitility;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.teamcity.api.enums.Endpoint.BUILDS;
import static com.example.teamcity.api.enums.Endpoint.BUILD_QUEUE;

public final class AsyncConditions {
    private static final String ATTR_NAME = "build";

    public static void waitUntilBuildFinished(CheckedRequests req, String buildId) {
        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    Build build = (Build) req.getRequest(BUILD_QUEUE).read("id:" + buildId);
                    return build.getState().equals("finished");
                });
    }

    public static void waitUntilBuildFinished(CheckedRequests req, BuildType buildType) {
        Awaitility.await()
                .atMost(1, TimeUnit.MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    Optional<Build> createdBuildRun = req.<Build>getRequest(BUILDS).findAll(ATTR_NAME)
                            .stream()
                            .filter(item -> item.getBuildTypeId().equals(buildType.getId()))
                            .findFirst();
                    if (createdBuildRun.isPresent()){
                        Build build = (Build) req.getRequest(BUILD_QUEUE).read("id:" + createdBuildRun.get().getId());
                        return build.getState().equals("finished");
                    } else {
                        return false;
                    }
                });
    }
}
