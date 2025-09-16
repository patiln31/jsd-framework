package org.jsd.listeners;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsd.base.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotListener implements ITestListener {
    private static final Logger log = LogManager.getLogger(ScreenshotListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver("chrome"); // Get current driver
        
        if (driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                
                // Attach to Allure report
                Allure.addAttachment("Screenshot on Failure", "image/png", 
                    new ByteArrayInputStream(screenshot), "png");
                
                // Also save to file system with dynamic path
                saveScreenshotToFile(screenshot, result.getMethod().getMethodName());
                
                log.info("Screenshot captured and attached to Allure report for failed test: {}", 
                    result.getMethod().getMethodName());
            } catch (Exception e) {
                log.error("Failed to capture screenshot: {}", e.getMessage());
            }
        }
    }
    
    private void saveScreenshotToFile(byte[] screenshot, String testName) {
        try {
            // Create dynamic screenshot directory
            String userDir = System.getProperty("user.dir");
            Path screenshotDir = Paths.get(userDir, "test-output", "screenshots");
            
            // Create directory if it doesn't exist
            if (!Files.exists(screenshotDir)) {
                Files.createDirectories(screenshotDir);
            }
            
            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = String.format("%s_%s.png", testName, timestamp);
            Path screenshotPath = screenshotDir.resolve(fileName);
            
            // Write screenshot to file
            Files.write(screenshotPath, screenshot);
            log.info("Screenshot saved to: {}", screenshotPath.toString());
            
        } catch (IOException e) {
            log.error("Failed to save screenshot to file: {}", e.getMessage());
        }
    }
}