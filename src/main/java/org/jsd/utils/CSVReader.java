package org.jsd.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading CSV files and converting them to TestNG data provider format.
 * 
 * This class provides functionality to:
 * - Read CSV files with comma-separated values
 * - Skip header rows automatically
 * - Convert data to Object[][] format for TestNG data providers
 * - Handle file reading errors gracefully
 * - Trim whitespace from values
 * 
 * Expected CSV format:
 * Header1,Header2,Header3
 * value1,value2,value3
 * value4,value5,value6
 * 
 * Usage:
 * Object[][] data = CSVReader.readCSVData("path/to/file.csv");
 * 
 * @author JSD Framework Team
 * @version 1.0
 */
public class CSVReader {
    /** Logger instance for this class */
    private static final Logger log = LogManager.getLogger(CSVReader.class);

    /**
     * Reads CSV file and converts it to Object[][] format for TestNG data providers.
     * 
     * This method:
     * 1. Opens and reads the specified CSV file
     * 2. Skips the first row (assumed to be headers)
     * 3. Splits each line by comma and trims whitespace
     * 4. Converts the data to Object[][] format
     * 5. Logs the number of rows read
     * 
     * @param filePath Path to the CSV file to read
     * @return Object[][] containing the CSV data, or empty array if file reading fails
     */
    public static Object[][] readCSVData(String filePath) {
        List<String[]> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            // Read file line by line
            while ((line = br.readLine()) != null) {
                // Skip header row (first line)
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Split line by comma and clean up values
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim(); // Remove leading/trailing whitespace
                }
                data.add(values);
            }
            
            log.info("Successfully read {} rows from CSV file: {}", data.size(), filePath);
            
        } catch (IOException e) {
            log.error("Failed to read CSV file: {}", filePath, e);
            return new Object[0][0];
        }
        
        // Convert List<String[]> to Object[][] for TestNG compatibility
        Object[][] result = new Object[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            result[i] = data.get(i);
        }
        
        return result;
    }
}