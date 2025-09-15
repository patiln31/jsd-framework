package org.jsd.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final Logger log = LogManager.getLogger(BasePage.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
        PageFactory.initElements(driver, this);
    }

    protected void click(By locator) {
        try {
            WebElement element = waitForVisibility(locator);
            element.click();
            log.debug("Clicked element: {}", locator);
        } catch (Exception e) {
            log.error("Failed to click element: {}", locator, e);
            throw e;
        }
    }

    protected void type(By locator, String text) {
        try {
            WebElement element = waitForVisibility(locator);
            element.clear();
            element.sendKeys(text);
            log.debug("Typed text '{}' into element: {}", text, locator);
        } catch (Exception e) {
            log.error("Failed to type text into element: {}", locator, e);
            throw e;
        }
    }

    protected String getText(By locator) {
        try {
            WebElement element = waitForVisibility(locator);
            String text = element.getText();
            log.debug("Got text '{}' from element: {}", text, locator);
            return text;
        } catch (Exception e) {
            log.error("Failed to get text from element: {}", locator, e);
            throw e;
        }
    }

    protected WebElement waitForVisibility(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            log.error("Element not visible: {}", locator, e);
            throw e;
        }
    }

    protected void selectByValue(By locator, String value) {
        try {
            WebElement element = waitForVisibility(locator);
            Select select = new Select(element);
            select.selectByValue(value);
            log.debug("Selected value '{}' from dropdown: {}", value, locator);
        } catch (Exception e) {
            log.error("Failed to select value from dropdown: {}", locator, e);
            throw e;
        }
    }

    protected void selectByVisibleText(By locator, String text) {
        try {
            WebElement element = waitForVisibility(locator);
            Select select = new Select(element);
            select.selectByVisibleText(text);
            log.debug("Selected text '{}' from dropdown: {}", text, locator);
        } catch (Exception e) {
            log.error("Failed to select text from dropdown: {}", locator, e);
            throw e;
        }
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (Exception e) {
            log.debug("Element not displayed: {}", locator);
            return false;
        }
    }

    protected String getAttributeValue(By locator, String attribute) {
        try {
            WebElement element = waitForVisibility(locator);
            String value = element.getAttribute(attribute);
            log.debug("Got attribute '{}' value '{}' from element: {}", attribute, value, locator);
            return value;
        } catch (Exception e) {
            log.error("Failed to get attribute from element: {}", locator, e);
            throw e;
        }
    }
}
