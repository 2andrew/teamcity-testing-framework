package com.example.teamcity.common;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.BaseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.common.ContentTypes.CONTENT_TYPE;

public final class WireMockInstance {
    private static final int PORT = Integer.parseInt(Config.getProperty("mockServerPort"));
    private static final Logger logger = LoggerFactory.getLogger(WireMockInstance.class);

    private static WireMockServer wireMockServer = null;

    private WireMockInstance() {
    }

    @SneakyThrows
    public static void setupServer(MappingBuilder mappingBuilder, int status, BaseModel model) {
        logger.info("Starting WireMock...");
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(PORT);
            configureFor("http://" + Config.getProperty("host").split(":")[0], PORT);
            wireMockServer.start();
        }
        logger.info("WireMock Server started at {}", wireMockServer.baseUrl());

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