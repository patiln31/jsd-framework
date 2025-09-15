package org.jsd.base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsd.utils.ConfigReader;

public class BaseTest {
    protected WebDriver driver;
    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    @Parameters({"browser"})
    @BeforeMethod
    public void setUp(@Optional("chrome") String browser) {
        try {
            driver = DriverFactory.getDriver(browser);
            driver.get(ConfigReader.get("base.url"));
            log.info("Driver initialized successfully for browser: {}", browser);
        } catch (Exception e) {
            log.error("Failed to initialize driver for browser: {}", browser, e);
            throw e;
        }
    }

    @AfterMethod
    public void tearDown() {
        try {
            DriverFactory.quitDriver(); // This handles both driver quit and thread cleanup
            log.info("Driver closed successfully");
        } catch (Exception e) {
            log.error("Failed to close driver", e);
            throw e;
        }
    }
}