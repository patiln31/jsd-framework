package org.jsd.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.jsd.utils.ConfigReader;
import java.net.URL;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver(String browserType) {
        if (driverThreadLocal.get() == null) {
            WebDriver driver = createDriver(browserType);
            driverThreadLocal.set(driver);
        }
        return driverThreadLocal.get();
    }

    private static WebDriver createDriver(String browserType) {
        WebDriver driver;
        
        // Check if remote execution is enabled
        boolean isRemote = Boolean.parseBoolean(ConfigReader.get("remote", "false"));
        String gridUrl = System.getProperty("grid.url", ConfigReader.get("grid.url"));
        
        if (isRemote && gridUrl != null) {
            // Remote execution on Grid
            driver = createRemoteDriver(browserType, gridUrl);
        } else {
            // Local execution
            driver = createLocalDriver(browserType);
        }
        
        driver.manage().window().maximize();
        return driver;
    }
    
    private static WebDriver createRemoteDriver(String browserType, String gridUrl) {
        try {
            URL hubUrl = new URL(gridUrl);
            return switch (browserType.toLowerCase()) {
                case "chrome" -> new RemoteWebDriver(hubUrl, new ChromeOptions());
                case "edge" -> new RemoteWebDriver(hubUrl, new EdgeOptions());
                case "firefox" -> new RemoteWebDriver(hubUrl, new FirefoxOptions());
                default -> throw new IllegalArgumentException("Unsupported browser: " + browserType);
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to create remote driver", e);
        }
    }
    
    private static WebDriver createLocalDriver(String browserType) {
        return switch (browserType.toLowerCase()) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                yield new ChromeDriver();
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                yield new EdgeDriver();
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver();
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserType);
        };
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
