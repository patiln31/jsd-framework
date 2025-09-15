package org.jsd.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonReader {
    private static final Logger log = LogManager.getLogger(JsonReader.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String TEST_DATA_PATH = "src/test/resources/testdata/testdata.json";
    
    private static JsonNode testData;
    
    static {
        try {
            testData = mapper.readTree(new File(TEST_DATA_PATH));
            log.info("Test data loaded successfully from: {}", TEST_DATA_PATH);
        } catch (IOException e) {
            log.error("Failed to load test data file: {}", TEST_DATA_PATH, e);
            throw new RuntimeException("Test data file not found", e);
        }
    }

    public static <T> T getData(String key, Class<T> valueType) {
        try {
            JsonNode node = testData.get(key);
            if (node == null) {
                log.warn("Test data key not found: {}", key);
                return null;
            }
            return mapper.treeToValue(node, valueType);
        } catch (IOException e) {
            log.error("Failed to parse test data for key: {}", key, e);
            throw new RuntimeException("Error parsing test data", e);
        }
    }

    public static Map<String, Object> getDataAsMap(String key) {
        try {
            JsonNode node = testData.get(key);
            if (node == null) {
                log.warn("Test data key not found: {}", key);
                return null;
            }
            return mapper.convertValue(node, Map.class);
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert test data to Map for key: {}", key, e);
            throw new RuntimeException("Error converting test data", e);
        }
    }
}
