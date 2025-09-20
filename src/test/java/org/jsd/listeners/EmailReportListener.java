package org.jsd.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsd.listeners.ScreenshotListener;
import org.jsd.utils.ConfigReader;
import org.jsd.utils.EmailReportGenerator;
import org.jsd.utils.EmailSender;
import org.testng.ISuiteListener;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class EmailReportListener implements ISuiteListener {
    private static final Logger log = LogManager.getLogger(EmailReportListener.class);
    private LocalDateTime suiteStartTime;

    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = LocalDateTime.now();
        ScreenshotListener.clearFailedScreenshots(); // Clear previous screenshots
        log.info("🚀 EMAIL LISTENER: Test suite started: {}", suite.getName());
        System.out.println("🚀 EMAIL LISTENER: Suite started - " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        // Calculate execution time
        LocalDateTime suiteEndTime = LocalDateTime.now();
        Duration duration = Duration.between(suiteStartTime, suiteEndTime);
        String executionTime = String.format("%d min %d sec", 
            duration.toMinutes(), duration.getSeconds() % 60);

        // Collect test results
        int totalTests = 0, passed = 0, failed = 0, skipped = 0;
        
        Map<String, ISuiteResult> results = suite.getResults();
        for (ISuiteResult result : results.values()) {
            passed += result.getTestContext().getPassedTests().size();
            failed += result.getTestContext().getFailedTests().size();
            skipped += result.getTestContext().getSkippedTests().size();
        }
        totalTests = passed + failed + skipped; // Actual executed tests

        // Get environment info
        String environment = System.getProperty("execution.env", "Local");
        String browser = "Chrome/Edge"; // You can make this dynamic based on actual browser used

        // Get failed screenshots and convert to EmailReportGenerator format
        var screenshotData = ScreenshotListener.getFailedScreenshots();
        java.util.List<EmailReportGenerator.FailedTestScreenshot> failedScreenshots = new java.util.ArrayList<>();
        java.util.List<String> screenshotPaths = new java.util.ArrayList<>();
        
        for (var screenshot : screenshotData) {
            failedScreenshots.add(new EmailReportGenerator.FailedTestScreenshot(
                screenshot.testName, screenshot.base64Screenshot, screenshot.timestamp));
            if (screenshot.filePath != null) {
                screenshotPaths.add(screenshot.filePath);
            }
        }
        
        // Generate HTML report
        String htmlReport = EmailReportGenerator.generateHTMLReport(
            totalTests, passed, failed, skipped, executionTime, environment, browser, failedScreenshots);

        log.info("Email report generated - Total: {}, Passed: {}, Failed: {}, Skipped: {}", 
            totalTests, passed, failed, skipped);
        
        // Check if email notification is enabled
        boolean emailEnabled = Boolean.parseBoolean(ConfigReader.getProperty("email.notification.enabled", "false"));
        
        System.out.println("📧 EMAIL LISTENER: Email enabled = " + emailEnabled);
        if (emailEnabled) {
            try {
                System.out.println("📧 EMAIL LISTENER: Attempting to send email...");
                EmailSender.sendTestReport(htmlReport, totalTests, passed, failed);
                log.info("✅ Test report email sent successfully");
                System.out.println("✅ EMAIL SENT SUCCESSFULLY!");
            } catch (Exception e) {
                log.error("❌ Failed to send email report: {}", e.getMessage());
                System.out.println("❌ EMAIL FAILED: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.info("Email notification disabled - HTML report saved locally only");
            System.out.println("📧 EMAIL DISABLED in config");
        }
    }
}
