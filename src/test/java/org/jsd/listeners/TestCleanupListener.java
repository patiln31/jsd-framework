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
            // Clean allure-results directory for fresh reports
            Path allureResults = Paths.get("allure-results");
            if (Files.exists(allureResults)) {
                Files.walk(allureResults)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
                log.info("Cleaned allure-results directory for fresh reports");
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
            
            // Clean screenshots directory
            Path screenshotsDir = Paths.get(System.getProperty("user.dir"), "test-output", "screenshots");
            if (Files.exists(screenshotsDir)) {
                Files.walk(screenshotsDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
                log.info("Cleaned screenshots directory");
            }
            
            // Clean email reports directory
            Path emailReportsDir = Paths.get(System.getProperty("user.dir"), "test-output", "email-reports");
            if (Files.exists(emailReportsDir)) {
                Files.walk(emailReportsDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
                log.info("Cleaned email-reports directory");
            }
            
        } catch (IOException e) {
            log.warn("Failed to clean old results: {}", e.getMessage());
        }
    }
}