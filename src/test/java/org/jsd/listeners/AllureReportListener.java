package org.jsd.listeners;

import org.testng.ISuiteListener;
import org.testng.ISuite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class AllureReportListener implements ISuiteListener {
    private static final Logger log = LogManager.getLogger(AllureReportListener.class);
    private static final String ALLURE_RESULTS_PATH = "allure-results";
    private static final String ALLURE_REPORT_PATH = "allure-report";
    
    @Override
    public void onFinish(ISuite suite) {
        log.info("Test suite '{}' finished. Generating Allure report...", suite.getName());
        
        try {
            // Generate Allure report
            generateAllureReport();
            
            // Wait a moment for report generation to complete
            Thread.sleep(2000);
            
            // Open the report in default browser
            openAllureReport();
            
        } catch (Exception e) {
            log.error("Failed to generate or open Allure report", e);
        }
    }
    
    private void generateAllureReport() throws IOException, InterruptedException {
        log.info("Starting Allure server (will auto-stop in 10 seconds)...");
        
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd", "/c", "start", "cmd", "/c", "allure serve " + ALLURE_RESULTS_PATH + " & timeout /t 10 /nobreak >nul & taskkill /f /im cmd.exe /fi \"WINDOWTITLE eq *allure*\"");
        
        Process process = processBuilder.start();
        log.info("Allure server started");
    }
    
    private void openAllureReport() {
        // No need to open manually - allure serve opens automatically
        log.info("Allure report will open automatically in browser");
    }
}