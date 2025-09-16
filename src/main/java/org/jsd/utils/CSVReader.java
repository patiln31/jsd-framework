package org.jsd.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private static final Logger log = LogManager.getLogger(CSVReader.class);
    
    public static Object[][] readCSVData(String filePath) {
        List<String[]> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                data.add(line.split(","));
            }
            
            Object[][] result = new Object[data.size()][];
            for (int i = 0; i < data.size(); i++) {
                result[i] = data.get(i);
            }
            
            log.info("CSV data read successfully from: {}", filePath);
            return result;
            
        } catch (IOException e) {
            log.error("Failed to read CSV file: {}", filePath, e);
            throw new RuntimeException("CSV file not found", e);
        }
    }
}