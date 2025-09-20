package org.jsd.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsd.utils.ConfigReader;
import org.jsd.utils.EmailReportGenerator;
import org.jsd.utils.EmailSender;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class UniversalEmailListener implements ITestListener {
    private static final Logger log = LogManager.getLogger(UniversalEmailListener.class);
    private static final AtomicInteger totalTests = new AtomicInteger(0);
    private static final AtomicInteger passedTests = new AtomicInteger(0);
    private static final AtomicInteger failedTests = new AtomicInteger(0);
    private static final AtomicBoolean emailSent = new AtomicBoolean(false);
    private static LocalDateTime startTime = LocalDateTime.now();

    @Override
    public void onTestStart(ITestResult result) {
        if (totalTests.get() == 0) {
            startTime = LocalDateTime.now();
            log.info("🚀 UNIVERSAL EMAIL LISTENER: Test execution started");
            System.out.println("🚀 UNIVERSAL EMAIL LISTENER: Test execution started");
        }
        totalTests.incrementAndGet();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests.incrementAndGet();
        checkAndSendEmail();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedTests.incrementAndGet();
        checkAndSendEmail();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        checkAndSendEmail();
    }

    private synchronized void checkAndSendEmail() {
        // Wait a bit to see if more tests are coming
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if email notification is enabled
        boolean emailEnabled = Boolean.parseBoolean(ConfigReader.getProperty("email.notification.enabled", "false"));
        
        if (!emailEnabled) {
            log.info("📧 Email notification disabled in config");
            return;
        }

        // Send email only once and only if we have completed tests
        if (!emailSent.get() && (passedTests.get() + failedTests.get()) > 0) {
            emailSent.set(true);
            sendEmailReport();
        }
    }

    private void sendEmailReport() {
        try {
            System.out.println("📧 UNIVERSAL EMAIL LISTENER: Preparing email report...");
            
            int total = totalTests.get();
            int passed = passedTests.get();
            int failed = failedTests.get();
            int skipped = total - passed - failed;
            
            LocalDateTime endTime = LocalDateTime.now();
            long duration = java.time.Duration.between(startTime, endTime).getSeconds();
            String executionTime = String.format("%d min %d sec", duration / 60, duration % 60);
            
            // Get failed screenshots
            var screenshotData = ScreenshotListener.getFailedScreenshots();
            java.util.List<EmailReportGenerator.FailedTestScreenshot> failedScreenshots = new java.util.ArrayList<>();
            
            for (var screenshot : screenshotData) {
                failedScreenshots.add(new EmailReportGenerator.FailedTestScreenshot(
                    screenshot.testName, screenshot.base64Screenshot, screenshot.timestamp));
            }
            
            // Generate HTML report
            String htmlReport = EmailReportGenerator.generateHTMLReport(
                total, passed, failed, skipped, executionTime, "Local", "Chrome/Edge", failedScreenshots);

            System.out.println("📧 UNIVERSAL EMAIL LISTENER: Sending email...");
            EmailSender.sendTestReport(htmlReport, total, passed, failed);
            
            log.info("✅ Universal email report sent successfully - Total: {}, Passed: {}, Failed: {}", 
                total, passed, failed);
            System.out.println("✅ EMAIL SENT SUCCESSFULLY!");
            
        } catch (Exception e) {
            log.error("❌ Failed to send universal email report: {}", e.getMessage());
            System.out.println("❌ EMAIL FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
