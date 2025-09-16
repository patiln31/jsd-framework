package org.jsd.listeners;

import org.testng.ISuiteListener;
import org.testng.ISuite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class TestCleanupListener implements ISuiteListener {
    private static final Logger log = LogManager.getLogger(TestCleanupListener.class);
    
    @Override
    public void onStart(ISuite suite) {
        log.info("Cleaning old test results before starting suite: {}", suite.getName());
        cleanOldResults();
    }
    
    private void cleanOldResults() {
        try {
            // Clean allure-results directory
            Path allureResults = Paths.get("allure-results");
            if (Files.exists(allureResults)) {
                Files.walk(allureResults)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
                log.info("Cleaned allure-results directory");
            }
            
            // Clean allure-report directory
            Path allureReport = Paths.get("allure-report");
            if (Files.exists(allureReport)) {
                Files.walk(allureReport)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
                log.info("Cleaned allure-report directory");
            }
            
        } catch (IOException e) {
            log.warn("Failed to clean old results: {}", e.getMessage());
        }
    }
}