package org.jsd.listeners;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class EnvironmentListener implements ITestListener {
    private static final Logger log = LogManager.getLogger(EnvironmentListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        // Add environment information to each test
        Allure.parameter("OS", System.getProperty("os.name"));
        Allure.parameter("OS Version", System.getProperty("os.version"));
        Allure.parameter("Java Version", System.getProperty("java.version"));
        Allure.parameter("User", System.getProperty("user.name"));
        
        // Add execution environment
        String environment = System.getProperty("execution.env", "Local");
        Allure.parameter("Execution Environment", environment);
        
        log.info("Test started on OS: {} | Java: {} | Environment: {}", 
            System.getProperty("os.name"), 
            System.getProperty("java.version"),
            environment);
    }
}