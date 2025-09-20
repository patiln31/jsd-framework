package org.jsd.utils;

import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Visual testing utilities for screenshot comparison and baseline management.
 * 
 * This class provides functionality for:
 * - Screenshot capture and comparison
 * - Baseline image management (create, update)
 * - Automatic diff image generation
 * - Allure report integration with visual attachments
 * - Configurable pixel difference threshold
 * 
 * Directory structure:
 * - test-output/visual-baselines/ : Baseline images
 * - test-output/visual-actual/    : Current test screenshots
 * - test-output/visual-diff/      : Difference images
 * 
 * Usage:
 * VisualTestingUtils.compareScreenshot(driver, "login-page");
 * VisualTestingUtils.updateBaseline(driver, "homepage");
 * 
 * @author JSD Framework Team
 * @version 1.0
 */
public class VisualTestingUtils {
    /** Logger instance for this class */
    private static final Logger log = LogManager.getLogger(VisualTestingUtils.class);
    
    /** Directory path for storing baseline images */
    private static final String BASELINE_PATH = "test-output/visual-baselines/";
    
    /** Directory path for storing actual test screenshots */
    private static final String ACTUAL_PATH = "test-output/visual-actual/";
    
    /** Directory path for storing difference images */
    private static final String DIFF_PATH = "test-output/visual-diff/";
    
    // Static initialization block to create required directories
    static {
        createDirectories();
    }
    
    /**
     * Creates the required directory structure for visual testing.
     * 
     * This method is called during class initialization to ensure
     * all necessary directories exist before any visual testing operations.
     */
    private static void createDirectories() {
        try {
            Files.createDirectories(Paths.get(BASELINE_PATH));
            Files.createDirectories(Paths.get(ACTUAL_PATH));
            Files.createDirectories(Paths.get(DIFF_PATH));
        } catch (IOException e) {
            log.error("Failed to create visual testing directories", e);
        }
    }
    
    /**
     * Compares current screenshot with baseline image and generates diff if different.
     * 
     * This method:
     * 1. Takes a screenshot of the current page
     * 2. Compares it with the baseline image
     * 3. Creates a baseline if none exists
     * 4. Generates a diff image highlighting differences
     * 5. Attaches all images to Allure report
     * 
     * @param driver The WebDriver instance to capture screenshot from
     * @param testName Unique identifier for the test/page being compared
     * @return true if images match within threshold, false otherwise
     */
    @Step("Visual comparison: {testName}")
    public static boolean compareScreenshot(WebDriver driver, String testName) {
        try {
            byte[] actualScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String actualPath = ACTUAL_PATH + testName + ".png";
            String baselinePath = BASELINE_PATH + testName + ".png";
            
            // Save actual screenshot
            Files.write(Paths.get(actualPath), actualScreenshot);
            
            // Check if baseline exists
            if (!Files.exists(Paths.get(baselinePath))) {
                log.warn("Baseline image not found for '{}'. Creating baseline from current screenshot.", testName);
                Files.copy(Paths.get(actualPath), Paths.get(baselinePath));
                attachScreenshot(actualScreenshot, "Baseline Created: " + testName);
                return true;
            }
            
            // Compare images
            BufferedImage baselineImage = ImageIO.read(new File(baselinePath));
            BufferedImage actualImage = ImageIO.read(new ByteArrayInputStream(actualScreenshot));
            
            boolean imagesMatch = compareImages(baselineImage, actualImage, testName);
            
            if (imagesMatch) {
                log.info("✅ Visual comparison passed for '{}'", testName);
                attachScreenshot(actualScreenshot, "Visual Test Passed: " + testName);
            } else {
                log.error("❌ Visual comparison failed for '{}'", testName);
                attachScreenshot(Files.readAllBytes(Paths.get(baselinePath)), "Baseline: " + testName);
                attachScreenshot(actualScreenshot, "Actual: " + testName);
                
                // Create diff image if possible
                createDiffImage(baselineImage, actualImage, testName);
            }
            
            return imagesMatch;
            
        } catch (Exception e) {
            log.error("Visual comparison failed for '{}': {}", testName, e.getMessage());
            return false;
        }
    }
    
    private static boolean compareImages(BufferedImage baseline, BufferedImage actual, String testName) {
        if (baseline.getWidth() != actual.getWidth() || baseline.getHeight() != actual.getHeight()) {
            log.warn("Image dimensions differ for '{}': Baseline({}x{}), Actual({}x{})", 
                testName, baseline.getWidth(), baseline.getHeight(), actual.getWidth(), actual.getHeight());
            return false;
        }
        
        int width = baseline.getWidth();
        int height = baseline.getHeight();
        int diffPixels = 0;
        int totalPixels = width * height;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (baseline.getRGB(x, y) != actual.getRGB(x, y)) {
                    diffPixels++;
                }
            }
        }
        
        double diffPercentage = (double) diffPixels / totalPixels * 100;
        log.info("Visual comparison for '{}': {:.2f}% pixels differ", testName, diffPercentage);
        
        // Allow 1% difference threshold
        return diffPercentage <= 1.0;
    }
    
    private static void createDiffImage(BufferedImage baseline, BufferedImage actual, String testName) {
        try {
            int width = Math.min(baseline.getWidth(), actual.getWidth());
            int height = Math.min(baseline.getHeight(), actual.getHeight());
            
            BufferedImage diffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int baselinePixel = baseline.getRGB(x, y);
                    int actualPixel = actual.getRGB(x, y);
                    
                    if (baselinePixel != actualPixel) {
                        // Highlight differences in red
                        diffImage.setRGB(x, y, 0xFF0000);
                    } else {
                        // Keep original pixel
                        diffImage.setRGB(x, y, baselinePixel);
                    }
                }
            }
            
            String diffPath = DIFF_PATH + testName + "_diff.png";
            ImageIO.write(diffImage, "PNG", new File(diffPath));
            
            // Attach diff image to Allure
            byte[] diffBytes = Files.readAllBytes(Paths.get(diffPath));
            attachScreenshot(diffBytes, "Diff Image: " + testName);
            
        } catch (Exception e) {
            log.error("Failed to create diff image for '{}': {}", testName, e.getMessage());
        }
    }
    
    @Step("Update baseline image: {testName}")
    public static void updateBaseline(WebDriver driver, String testName) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String baselinePath = BASELINE_PATH + testName + ".png";
            Files.write(Paths.get(baselinePath), screenshot);
            log.info("✅ Baseline updated for '{}'", testName);
            attachScreenshot(screenshot, "Updated Baseline: " + testName);
        } catch (Exception e) {
            log.error("Failed to update baseline for '{}': {}", testName, e.getMessage());
        }
    }
    
    @Attachment(value = "{attachmentName}", type = "image/png")
    private static byte[] attachScreenshot(byte[] screenshot, String attachmentName) {
        return screenshot;
    }
}