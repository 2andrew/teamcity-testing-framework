package com.example.teamcity.api.custom;

import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.requests.CheckedRequests;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.teamcity.api.enums.Endpoint.BUILDS;
import static com.example.teamcity.api.enums.Endpoint.BUILD_QUEUE;

public final class AsyncConditions {
    private static final Logger logger = LoggerFactory.getLogger(AsyncConditions.class);
    private static final String ATTR_NAME = "build";

    public static String waitUntilBuildFinishedByType(CheckedRequests req, BuildType buildType) {
        return Awaitility.await()
                .atMost(3, TimeUnit.MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> {
                    Optional<Build> createdBuildRun = req.<Build>getRequest(BUILDS).findAll(ATTR_NAME)
                            .stream()
                            .filter(item -> item.getBuildTypeId().equals(buildType.getId()))
                            .findFirst();

                    if (createdBuildRun.isPresent()) {
                        Build build = (Build) req.getRequest(BUILD_QUEUE).read("id:" + createdBuildRun.get().getId());
                        logger.info("Build state: " + build.getState() + ", status: " + build.getStatus());
                        if ("finished".equals(build.getState())) {
                            return createdBuildRun.get().getId();
                        }
                    }
                    return null;
                }, Objects::nonNull);
    }
}
