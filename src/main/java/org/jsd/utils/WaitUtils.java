package org.jsd.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WaitUtils {
    private static final Logger log = LogManager.getLogger(WaitUtils.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_POLLING = Duration.ofMillis(500);
    
    public static WebElement waitForElementVisible(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("Element not visible: {}", locator, e);
            throw e;
        }
    }
    
    public static WebElement waitForElementClickable(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            log.error("Element not clickable: {}", locator, e);
            throw e;
        }
    }
    
    public static boolean waitForElementInvisible(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("Element still visible: {}", locator, e);
            throw e;
        }
    }
    
    public static WebElement waitForElementWithFluentWait(WebDriver driver, By locator) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
            .withTimeout(DEFAULT_TIMEOUT)
            .pollingEvery(DEFAULT_POLLING)
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class);
            
        try {
            return wait.until(d -> d.findElement(locator));
        } catch (TimeoutException e) {
            log.error("Element not found with fluent wait: {}", locator, e);
            throw e;
        }
    }
    
    public static void waitForPageLoad(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
        } catch (TimeoutException e) {
            log.error("Page load timeout", e);
            throw e;
        }
    }
    
    public static void waitForAjax(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return jQuery.active == 0"));
        } catch (TimeoutException e) {
            log.error("Ajax calls timeout", e);
            throw e;
        }
    }
    
    public static void waitForCustomCondition(WebDriver driver, 
            java.util.function.Function<WebDriver, Boolean> condition) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
            wait.until(condition);
        } catch (TimeoutException e) {
            log.error("Custom condition timeout", e);
            throw e;
        }
    }
}
