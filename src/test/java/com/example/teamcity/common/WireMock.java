package com.example.teamcity.common;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.BaseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import lombok.SneakyThrows;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.common.ContentTypes.CONTENT_TYPE;

public final class WireMock {
    private static final int PORT = 8175;

    private static volatile WireMockServer wireMockServer;

    private WireMock() {
    }

    @SneakyThrows
    public static void setupServer(MappingBuilder mappingBuilder, int status, BaseModel model) {
        if (wireMockServer == null) {
            synchronized (WireMock.class) {
                if (wireMockServer == null) {
                    wireMockServer = new WireMockServer(PORT);
                    wireMockServer.start();
                    configureFor("localhost", wireMockServer.port());
                    configureFor(Config.getProperty("host").split(":")[0], wireMockServer.port());
                }
            }
        }

        var jsonModel = new ObjectMapper().writeValueAsString(model);

        wireMockServer.stubFor(mappingBuilder
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody(jsonModel)));
    }

    public static void stopServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }

}