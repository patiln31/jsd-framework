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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ScreenshotListener implements ITestListener {
    private static final Logger log = LogManager.getLogger(ScreenshotListener.class);
    private static final List<FailedTestScreenshot> failedScreenshots = new ArrayList<>();
    
    public static class FailedTestScreenshot {
        public String testName;
        public String base64Screenshot;
        public String timestamp;
        public String filePath;
        
        public FailedTestScreenshot(String testName, String base64Screenshot, String timestamp, String filePath) {
            this.testName = testName;
            this.base64Screenshot = base64Screenshot;
            this.timestamp = timestamp;
            this.filePath = filePath;
        }
    }
    
    public static List<FailedTestScreenshot> getFailedScreenshots() {
        return new ArrayList<>(failedScreenshots);
    }
    
    public static void clearFailedScreenshots() {
        failedScreenshots.clear();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("📸 SCREENSHOT LISTENER: Test failed - " + result.getMethod().getMethodName());
        WebDriver driver = null;
        
        // Try to get driver from test instance first
        try {
            Object testInstance = result.getInstance();
            if (testInstance instanceof org.jsd.base.BaseTest) {
                org.jsd.base.BaseTest baseTest = (org.jsd.base.BaseTest) testInstance;
                driver = baseTest.getDriver();
                System.out.println("📸 Got driver from test instance");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not get driver from test instance: " + e.getMessage());
        }
        
        // Fallback to DriverFactory if needed
        if (driver == null) {
            try {
                driver = DriverFactory.getDriver("chrome");
                System.out.println("📸 Got driver from DriverFactory");
            } catch (Exception e) {
                System.out.println("❌ Could not get driver from DriverFactory: " + e.getMessage());
            }
        }
        
        if (driver != null) {
            try {
                System.out.println("📸 Taking screenshot...");
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                System.out.println("📸 Screenshot captured, size: " + screenshot.length + " bytes");
                
                // Universal screenshot attachment for both IntelliJ and Maven
                String testName = result.getMethod().getMethodName();
                
                // Method 1: Direct byte array attachment (works in IntelliJ)
                try {
                    String attachmentName = "Screenshot-" + testName + "-" + System.currentTimeMillis();
                    Allure.getLifecycle().addAttachment(attachmentName, "image/png", "png", screenshot);
                    System.out.println("✅ Direct screenshot attached to Allure: " + attachmentName);
                } catch (Exception e) {
                    System.out.println("⚠️ Direct attachment failed: " + e.getMessage());
                }
                
                // Method 2: File-based attachment (works in Maven)
                String screenshotPath = saveScreenshotToFile(screenshot, testName);
                if (screenshotPath != null) {
                    try {
                        Allure.addAttachment("Screenshot-File", "image/png", 
                            new java.io.FileInputStream(screenshotPath), "png");
                        System.out.println("✅ File screenshot attached to Allure: " + screenshotPath);
                    } catch (Exception e) {
                        System.out.println("⚠️ File attachment failed: " + e.getMessage());
                    }
                } else {
                    System.out.println("❌ Failed to save screenshot file");
                }
                
                // Method 3: @Attachment annotation (works in IntelliJ)
                attachScreenshotWithAnnotation(screenshot);
                
                // Save screenshot info for email
                String base64Screenshot = Base64.getEncoder().encodeToString(screenshot);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String filePath = saveScreenshotToFile(screenshot, testName);
                
                failedScreenshots.add(new FailedTestScreenshot(
                    testName, base64Screenshot, timestamp, filePath));
                
                System.out.println("✅ Screenshot saved for email attachment");
                log.info("Screenshot captured and attached to Allure report for failed test: {}", testName);
                
            } catch (Exception e) {
                System.out.println("❌ Failed to capture screenshot: " + e.getMessage());
                log.error("Failed to capture screenshot: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private String saveScreenshotToFile(byte[] screenshot, String testName) {
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
            
            return screenshotPath.toString();
            
        } catch (IOException e) {
            log.error("Failed to save screenshot to file: {}", e.getMessage());
            return null;
        }
    }
    
    @io.qameta.allure.Attachment(value = "Screenshot", type = "image/png")
    private byte[] attachScreenshotWithAnnotation(byte[] screenshot) {
        System.out.println("✅ @Attachment method called for IntelliJ");
        return screenshot;
    }
}