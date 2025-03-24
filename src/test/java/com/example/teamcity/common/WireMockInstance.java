package com.example.teamcity.common;

import com.example.teamcity.api.config.Config;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockWebServer;


public final class WireMockInstance {
    private static final int PORT = Integer.parseInt(Config.getProperty("wiremockPort"));
    private static MockWebServer mockWebServer;

    private WireMockInstance() {
    }

    @SneakyThrows
    public static void startServer(Dispatcher customDispatcher) {
        if (mockWebServer == null) {
            mockWebServer = new MockWebServer();
            mockWebServer.setDispatcher(customDispatcher);
            mockWebServer.start(PORT);
        }
    }

    @SneakyThrows
    public static void stopServer() {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
            mockWebServer = null;
        }
    }
}
