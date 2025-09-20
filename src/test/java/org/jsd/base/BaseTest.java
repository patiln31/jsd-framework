package org.jsd.base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsd.utils.ConfigReader;

/**
 * Base test class that provides common setup and teardown functionality
 * for all test classes in the framework.
 * 
 * This class handles:
 * - WebDriver initialization and configuration
 * - Browser setup based on parameters
 * - Navigation to base URL
 * - Cleanup and resource management
 * 
 * All test classes should extend this class to inherit common functionality.
 * 
 * @author JSD Framework Team
 * @version 1.0
 */
public class BaseTest {
    /** WebDriver instance shared across test methods */
    protected WebDriver driver;
    
    /** Logger instance for this class */
    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    /**
     * Sets up the test environment before each test method execution.
     * 
     * This method:
     * 1. Creates a WebDriver instance based on the specified browser
     * 2. Navigates to the base URL from configuration
     * 3. Logs the setup process for debugging
     * 
     * @param browser The browser type (chrome, firefox, edge) passed from TestNG XML
     *                Defaults to "chrome" if not specified
     */
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        try {
            // Initialize WebDriver using DriverFactory
            driver = DriverFactory.getDriver(browser);
            
            // Navigate to base URL from configuration
            driver.get(ConfigReader.getProperty("base.url"));
            log.info("Driver initialized successfully for browser: {}", browser);
        } catch (Exception e) {
            log.error("Failed to initialize driver for browser: {}", browser, e);
            throw e;
        }
    }

    /**
     * Cleans up resources after each test method execution.
     * 
     * This method:
     * 1. Closes the browser window
     * 2. Terminates the WebDriver session
     * 3. Releases system resources and thread-local variables
     */
    @AfterMethod
    public void tearDown() {
        try {
            // Quit driver and clean up thread-local variables
            DriverFactory.quitDriver();
            log.info("Driver closed successfully");
        } catch (Exception e) {
            log.error("Failed to close driver", e);
            throw e;
        }
    }
    
    /**
     * Provides access to the WebDriver instance for test classes.
     * 
     * @return The current WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }
}