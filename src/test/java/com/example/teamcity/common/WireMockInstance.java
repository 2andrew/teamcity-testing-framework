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

public final class WireMockInstance {
    private static final int PORT = Integer.parseInt(Config.getProperty("wiremockPort"));

    private static WireMockServer wireMockServer = null;

    private WireMockInstance() {
    }

    @SneakyThrows
    public static void setupServer(MappingBuilder mappingBuilder, int status, BaseModel model) {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(PORT);
            configureFor("localhost", PORT);
            configureFor("http://" + Config.getProperty("host").split(":")[0], PORT);
            wireMockServer.start();

            int retries = 5;
            while (!wireMockServer.isRunning() && retries > 0) {
                Thread.sleep(1000); // Wait 100ms
                retries--;
            }

            System.out.println("WireMock configured to use port: " + PORT);
            System.out.println("WireMock server started on port: " + wireMockServer.port() + ", running: " + wireMockServer.isRunning());
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