package org.jsd.listeners;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.IOException;
import java.lang.reflect.Field;

public class AllureReportListener implements ISuiteListener, ITestListener {
    private static final Logger log = LogManager.getLogger(AllureReportListener.class);
    private static final String ALLURE_RESULTS_PATH = "allure-results";
    private static WebDriver staticDriver; // For setDriver() method if needed
    
    public AllureReportListener() {
        // Allure screenshot listener initialized
    }
    
    // NOTE: If you need to set driver from test base, call AllureReportListener.setDriver(driver) in @BeforeMethod
    public static void setDriver(WebDriver driver) {
        staticDriver = driver;
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        log.info("Test failed: {}, capturing screenshot...", result.getMethod().getMethodName());
        captureScreenshot(result, "Failure Screenshot");
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        log.info("Test skipped: {}, capturing screenshot...", result.getMethod().getMethodName());
        captureScreenshot(result, "Skipped Test Screenshot");
    }
    
    private void captureScreenshot(ITestResult result, String screenshotName) {
        WebDriver driver = getDriverFromTest(result);
        
        if (driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String attachmentName = screenshotName + "-" + System.currentTimeMillis();
                Allure.getLifecycle().addAttachment(attachmentName, "image/png", "png", screenshot);
                log.info("Screenshot captured and attached to Allure report");
            } catch (Exception e) {
                log.warn("Failed to capture screenshot: {}", e.getMessage());
            }
        } else {
            log.warn("WebDriver is null, cannot capture screenshot for test: {}", result.getMethod().getMethodName());
        }
    }
    
    private WebDriver getDriverFromTest(ITestResult result) {
        WebDriver driver = null;
        
        try {
            // Method 1: Try to get driver from test instance (assuming public WebDriver driver field)
            Object testInstance = result.getInstance();
            if (testInstance != null) {
                // Try public field first
                try {
                    Field driverField = testInstance.getClass().getField("driver");
                    driver = (WebDriver) driverField.get(testInstance);
                    if (driver != null) {
                        log.debug("Driver obtained from public field");
                        return driver;
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // Field doesn't exist or not accessible, try other methods
                }
                
                // Method 2: Try BaseTest getDriver() method
                if (testInstance instanceof org.jsd.base.BaseTest) {
                    org.jsd.base.BaseTest baseTest = (org.jsd.base.BaseTest) testInstance;
                    driver = baseTest.getDriver();
                    if (driver != null) {
                        log.debug("Driver obtained from BaseTest.getDriver()");
                        return driver;
                    }
                }
                
                // Method 3: Try reflection to find any WebDriver field
                Field[] fields = testInstance.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (WebDriver.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        driver = (WebDriver) field.get(testInstance);
                        if (driver != null) {
                            log.debug("Driver obtained from private field: {}", field.getName());
                            return driver;
                        }
                    }
                }
            }
            
            // Method 4: Use static driver if set
            if (staticDriver != null) {
                log.debug("Using static driver set via setDriver()");
                return staticDriver;
            }
            
        } catch (Exception e) {
            log.warn("Error while trying to get WebDriver: {}", e.getMessage());
        }
        
        return null;
    }
    
    @Attachment(value = "{screenshotName}", type = "image/png")
    private byte[] attachScreenshot(byte[] screenshot, String screenshotName) {
        return screenshot;
    }
    
    @Override
    public void onFinish(ISuite suite) {
        log.info("Test suite '{}' finished. Generating Allure report...", suite.getName());
        
        try {
            // Generate and open Allure report automatically
            generateAllureReport();
        } catch (Exception e) {
            log.error("Failed to generate Allure report: {}", e.getMessage());
        }
    }
    
    private void generateAllureReport() throws IOException, InterruptedException {
        log.info("Generating Allure report...");
        
        // Wait a moment for all results to be written
        Thread.sleep(2000);
        
        // Use allure serve (single command that generates and opens)
        ProcessBuilder serveBuilder = new ProcessBuilder();
        serveBuilder.command("cmd", "/c", "start", "cmd", "/c", "allure serve " + ALLURE_RESULTS_PATH);
        serveBuilder.start();
        
        log.info("Allure report served and opened");
    }
    
    private void openAllureReport() {
        // No need to open manually - allure serve opens automatically
        log.info("Allure report will open automatically in browser");
    }
}