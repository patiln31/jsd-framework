package org.jsd.utils;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class CommonActions {
    private static final Logger log = LogManager.getLogger(CommonActions.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CommonActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Step("Click element with highlight: {0}")
    public void clickWithHighlight(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        highlightElement(locator);
        element.click();
        log.info("Clicked element with highlight: {}", locator);
    }

    @Step("Highlight element: {0}")
    public void highlightElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].style.border='3px solid yellow';", element);
        log.info("Highlighted element: {}", locator);
    }

    @Step("Verify element is displayed: {0}")
    public void verifyElementDisplayed(By locator) {
        Assert.assertTrue(isElementPresent(locator), "Element should be displayed: " + locator);
        log.info("Element is displayed: {}", locator);
    }

    @Step("Verify URL contains: {0}")
    public void verifyUrlContains(String expectedUrl) {
        String actualUrl = driver.getCurrentUrl();
        Assert.assertTrue(actualUrl.contains(expectedUrl), "URL should contain: " + expectedUrl);
        log.info("URL verified: {}", actualUrl);
    }

    private boolean isElementPresent(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}