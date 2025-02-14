package com.example.teamcity.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final String CONFIG_PROPERTIES = "config.properties";
    private static Config config;
    private final Properties properties;

    private Config() {
        properties = new Properties();
        loadProperties(CONFIG_PROPERTIES);
    }

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public static String getProperty(String key) {
        return getInstance().properties.getProperty(key);
    }

    private void loadProperties(String filename) {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                System.err.println("Config file " + filename + " not found");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Error while file reading" + filename + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
