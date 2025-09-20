package org.jsd.utils;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Custom assertion utilities that enhance standard TestNG assertions
 * with Allure reporting integration and improved logging.
 * 
 * This class provides:
 * - Element-based assertions (visibility, enabled state)
 * - Text validation assertions
 * - URL and page title assertions
 * - Allure step integration for better reporting
 * - Enhanced logging with visual indicators
 * 
 * All assertion methods are static and can be used directly without
 * creating an instance of this class.
 * 
 * Example usage:
 * CustomAssertions.assertElementDisplayed(loginButton, "Login Button");
 * CustomAssertions.assertUrlContains(driver, "/dashboard");
 * 
 * @author JSD Framework Team
 * @version 1.0
 */
public class CustomAssertions {
    /** Logger instance for this class */
    private static final Logger log = LogManager.getLogger(CustomAssertions.class);
    
    /**
     * Asserts that a web element is displayed on the page.
     * 
     * This method checks if the element is visible and throws an
     * AssertionError if it's not displayed. The assertion is logged
     * and appears as a step in Allure reports.
     * 
     * @param element The WebElement to check for visibility
     * @param elementName Descriptive name for the element (used in logging and reporting)
     * @throws AssertionError if the element is not displayed
     */
    @Step("Assert element is displayed: {elementName}")
    public static void assertElementDisplayed(WebElement element, String elementName) {
        try {
            Assert.assertTrue(element.isDisplayed(), elementName + " should be displayed");
            log.info("✅ Element '{}' is displayed as expected", elementName);
        } catch (Exception e) {
            log.error("❌ Element '{}' is not displayed", elementName);
            throw new AssertionError(elementName + " is not displayed", e);
        }
    }
    
    @Step("Assert element is not displayed: {elementName}")
    public static void assertElementNotDisplayed(WebElement element, String elementName) {
        try {
            Assert.assertFalse(element.isDisplayed(), elementName + " should not be displayed");
            log.info("✅ Element '{}' is not displayed as expected", elementName);
        } catch (Exception e) {
            log.info("✅ Element '{}' is not displayed (element not found)", elementName);
        }
    }
    
    @Step("Assert text equals: expected '{expectedText}'")
    public static void assertTextEquals(String actualText, String expectedText, String fieldName) {
        Assert.assertEquals(actualText, expectedText, 
            fieldName + " text mismatch. Expected: '" + expectedText + "', Actual: '" + actualText + "'");
        log.info("✅ Text assertion passed for '{}': '{}'", fieldName, expectedText);
    }
    
    @Step("Assert text contains: '{expectedText}'")
    public static void assertTextContains(String actualText, String expectedText, String fieldName) {
        Assert.assertTrue(actualText.contains(expectedText), 
            fieldName + " should contain '" + expectedText + "'. Actual text: '" + actualText + "'");
        log.info("✅ Text contains assertion passed for '{}': contains '{}'", fieldName, expectedText);
    }
    
    @Step("Assert URL contains: '{expectedUrlPart}'")
    public static void assertUrlContains(WebDriver driver, String expectedUrlPart) {
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains(expectedUrlPart), 
            "URL should contain '" + expectedUrlPart + "'. Current URL: '" + currentUrl + "'");
        log.info("✅ URL assertion passed: contains '{}'", expectedUrlPart);
    }
    
    @Step("Assert page title equals: '{expectedTitle}'")
    public static void assertPageTitle(WebDriver driver, String expectedTitle) {
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle, 
            "Page title mismatch. Expected: '" + expectedTitle + "', Actual: '" + actualTitle + "'");
        log.info("✅ Page title assertion passed: '{}'", expectedTitle);
    }
    
    @Step("Assert element is enabled: {elementName}")
    public static void assertElementEnabled(WebElement element, String elementName) {
        Assert.assertTrue(element.isEnabled(), elementName + " should be enabled");
        log.info("✅ Element '{}' is enabled as expected", elementName);
    }
    
    @Step("Assert element is disabled: {elementName}")
    public static void assertElementDisabled(WebElement element, String elementName) {
        Assert.assertFalse(element.isEnabled(), elementName + " should be disabled");
        log.info("✅ Element '{}' is disabled as expected", elementName);
    }
    
    @Step("Soft assert with message: {message}")
    public static void softAssert(boolean condition, String message) {
        if (!condition) {
            log.warn("⚠️ Soft assertion failed: {}", message);
        } else {
            log.info("✅ Soft assertion passed: {}", message);
        }
        // Note: For full soft assert functionality, consider using TestNG SoftAssert
        Assert.assertTrue(condition, message);
    }
}