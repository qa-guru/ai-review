package ai.review.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Env {
    private static Properties configProperties;
    
    static {
        loadConfigFile();
    }
    
    private Env() {}

    private static void loadConfigFile() {
        configProperties = new Properties();
        try {
            // Try to load from current directory first
            try (FileInputStream fis = new FileInputStream("config.properties")) {
                configProperties.load(fis);
            }
        } catch (IOException e) {
            // config.properties not found, that's okay - use environment variables only
            System.out.println("Note: config.properties not found, using environment variables only");
        }
    }

    public static String get(String key, String defaultValue) {
        // First try environment variable
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        
        // Then try config file
        String configValue = configProperties.getProperty(key);
        if (configValue != null && !configValue.isBlank()) {
            return configValue;
        }
        
        // Finally return default
        return defaultValue;
    }

    public static String require(String key, String message) {
        String value = get(key, null);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}


