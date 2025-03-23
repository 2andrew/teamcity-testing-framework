package com.example.teamcity.api;

import com.example.teamcity.api.requests.AgenAuthRequest;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.Test;

public class SetupTeamcityAgentTest extends BaseApiTest {
    private final String DEFAULT_NAME = "teamcityDefaultAgent";

    @Test(groups = {"Setup"})
    public void setupTeamCityAgentTest() {
        AgenAuthRequest agenAuthRequest = new AgenAuthRequest(Specifications.superUserSpec());
        agenAuthRequest.changeState(DEFAULT_NAME, true);
    }
}
