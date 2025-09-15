package org.jsd.listeners;

import org.testng.ISuiteListener;
import org.testng.ISuite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.ObjectInputFilter.Config;
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
        
        // Start Allure server
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "allure", "serve", ALLURE_RESULTS_PATH);
        pb.start();
        
        // Schedule termination after 10 seconds
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                Runtime.getRuntime().exec("taskkill /f /im java.exe /fi \"COMMANDLINE eq *allure*\"");
                log.info("Allure server terminated after 10 seconds");
            } catch (Exception e) {
                log.error("Failed to terminate Allure server", e);
            }
        }).start();
        
        log.info("Allure server started with 10-second auto-terminate");
    }
    
    private void openAllureReport() {
        // No need to open manually - allure serve opens automatically
        log.info("Allure report will open automatically in browser");
    }
}
