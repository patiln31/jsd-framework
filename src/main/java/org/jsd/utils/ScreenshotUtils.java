package org.jsd.utils;

import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    static {
        createScreenshotDirectory();
    }

    private static void createScreenshotDirectory() {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
        } catch (IOException e) {
            log.error("Failed to create screenshot directory", e);
        }
    }

    public static String captureScreenshot(WebDriver driver, String name) {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String fileName = String.format("%s_%s.png", name, timestamp);
            String filePath = Paths.get(SCREENSHOT_DIR, fileName).toString();

            // Take screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Path.of(filePath));

            // Attach to Allure
            attachScreenshotToAllure(Files.readAllBytes(screenshot.toPath()));

            log.info("Screenshot saved: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", name, e);
            return null;
        }
    }

    @Attachment(value = "Screenshot", type = "image/png")
    private static byte[] attachScreenshotToAllure(byte[] screenshot) {
        return screenshot;
    }

    public static String captureElementScreenshot(WebDriver driver, String name) {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String fileName = String.format("%s_%s.png", name, timestamp);
            String filePath = Paths.get(SCREENSHOT_DIR, fileName).toString();

            // Take screenshot of specific element
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(Path.of(filePath), screenshot);

            // Attach to Allure
            attachScreenshotToAllure(screenshot);

            log.info("Element screenshot saved: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to capture element screenshot: {}", name, e);
            return null;
        }
    }
}
