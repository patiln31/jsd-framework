package org.jsd.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {
    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_PATH = getConfigPath();
    
    private static String getConfigPath() {
        try {
            return ConfigReader.class.getClassLoader().getResource("configs/config.properties").getPath();
        } catch (Exception e) {
            // Fallback for different environments
            return "src/test/resources/configs/config.properties";
        }
    }
    
    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            log.info("Configuration loaded successfully from: {}", CONFIG_PATH);
        } catch (IOException e) {
            log.error("Failed to load configuration file: {}", CONFIG_PATH, e);
            throw new RuntimeException("Configuration file not found", e);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log.warn("Configuration key not found: {}", key);
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
