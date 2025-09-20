package org.jsd.listeners;

import org.jsd.utils.ConfigReader;
import org.jsd.utils.EmailSender;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleEmailListener implements ITestListener {
    private static final AtomicInteger totalTests = new AtomicInteger(0);
    private static final AtomicInteger passedTests = new AtomicInteger(0);
    private static final AtomicInteger failedTests = new AtomicInteger(0);
    private static final AtomicBoolean emailSent = new AtomicBoolean(false);
    private static final List<String> testResults = new ArrayList<>();
    private static LocalDateTime startTime;

    @Override
    public void onTestStart(ITestResult result) {
        if (totalTests.get() == 0) {
            startTime = LocalDateTime.now();
        }
        totalTests.incrementAndGet();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests.incrementAndGet();
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName().substring(result.getTestClass().getName().lastIndexOf('.') + 1);
        long duration = result.getEndMillis() - result.getStartMillis();
        testResults.add(String.format("✅ %s.%s - PASSED (%dms)", className, testName, duration));
        sendEmailIfNeeded();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedTests.incrementAndGet();
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName().substring(result.getTestClass().getName().lastIndexOf('.') + 1);
        String errorMsg = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
        long duration = result.getEndMillis() - result.getStartMillis();
        
        // Capture screenshot for failed test
        try {
            Object testInstance = result.getInstance();
            if (testInstance instanceof org.jsd.base.BaseTest) {
                org.jsd.base.BaseTest baseTest = (org.jsd.base.BaseTest) testInstance;
                if (baseTest.getDriver() != null) {
                    org.jsd.utils.ScreenshotUtils.captureScreenshot(baseTest.getDriver(), testName + "_FAILED");
                    System.out.println("📸 Screenshot captured for failed test: " + testName);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to capture screenshot: " + e.getMessage());
        }
        
        testResults.add(String.format("❌ %s.%s - FAILED (%dms) - %s 📸", className, testName, duration, errorMsg));
        sendEmailIfNeeded();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        sendEmailIfNeeded();
    }

    private synchronized void sendEmailIfNeeded() {
        // Check if email notification is enabled
        boolean emailEnabled = Boolean.parseBoolean(ConfigReader.getProperty("email.notification.enabled", "false"));
        
        if (!emailEnabled || emailSent.get()) {
            return;
        }

        // Wait a bit for other tests
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Send email only once and reset counters for next run
        if (!emailSent.getAndSet(true)) {
            sendSimpleEmail();
            // Reset for next execution
            totalTests.set(0);
            passedTests.set(0);
            failedTests.set(0);
            testResults.clear();
        }
    }

    private void sendSimpleEmail() {
        try {
            System.out.println("📧 SIMPLE EMAIL LISTENER: Sending email...");
            
            int total = totalTests.get();
            int passed = passedTests.get();
            int failed = failedTests.get();
            
            // Calculate execution time
            LocalDateTime endTime = LocalDateTime.now();
            long durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
            String executionTime = String.format("%dm %ds", durationSeconds / 60, durationSeconds % 60);
            
            // Build test results HTML
            StringBuilder resultsHtml = new StringBuilder();
            for (String testResult : testResults) {
                resultsHtml.append("<li>").append(testResult).append("</li>");
            }
            
            // Calculate success rate
            double successRate = total > 0 ? (double) passed / total * 100 : 0;
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            
            // Use beautiful HTML template with dynamic data
            String beautifulHtml = String.format(
                "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Test Execution Report</title>" +
                "<style>" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                "body { font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', Helvetica, Arial, sans-serif; background: #f5f5f7; line-height: 1.4; font-weight: 400; padding: 20px; color: #1d1d1f; }" +
                ".container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08); overflow: hidden; position: relative; }" +
                ".header { background: #ffffff; padding: 60px 25px 30px; text-align: center; position: relative; border-bottom: 1px solid #e5e5e7; }" +
                ".watermark { position: absolute; top: 15px; left: 15px; display: flex; align-items: center; gap: 6px; font-size: 0.65rem; color: #86868b; }" +
                ".watermark .avatar { width: 16px; height: 16px; background: linear-gradient(135deg, #007aff, #5856d6); border-radius: 50%%; display: flex; align-items: center; justify-content: center; color: white; font-size: 7px; font-weight: bold; }" +
                ".watermark .name { color: #007aff; font-weight: 600; }" +
                ".header h1 { font-size: 1.8rem; font-weight: 700; color: #1d1d1f; margin-bottom: 8px; letter-spacing: -0.02em; }" +
                ".header .subtitle { font-size: 0.9rem; color: #86868b; font-weight: 400; }" +
                ".summary { display: grid; grid-template-columns: repeat(4, 1fr); padding: 25px; gap: 15px; background: #fbfbfd; }" +
                ".metric { background: #ffffff; padding: 20px 15px; border-radius: 10px; text-align: center; border: 1px solid #e5e5e7; }" +
                ".metric .icon { font-size: 1.2rem; margin-bottom: 8px; }" +
                ".metric h3 { font-size: 1.8rem; font-weight: 700; color: #1d1d1f; margin-bottom: 4px; }" +
                ".metric p { font-size: 0.75rem; color: #86868b; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }" +
                ".progress-section { padding: 20px 25px; background: #fbfbfd; border-top: 1px solid #e5e5e7; }" +
                ".progress-label { font-size: 0.9rem; font-weight: 600; color: #1d1d1f; margin-bottom: 10px; text-align: center; }" +
                ".progress-bar { height: 6px; background: #e5e5e7; border-radius: 3px; overflow: hidden; }" +
                ".progress-fill { height: 100%%; background: #34c759; width: %.1f%%; border-radius: 3px; }" +
                ".details { padding: 25px; background: #ffffff; }" +
                ".detail-row { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid #f2f2f2; }" +
                ".detail-row:last-child { border-bottom: none; }" +
                ".detail-row .label { font-size: 0.85rem; color: #86868b; font-weight: 500; }" +
                ".detail-row .value { font-size: 0.9rem; color: #1d1d1f; font-weight: 600; }" +
                ".test-results { padding: 25px; background: #ffffff; border-top: 1px solid #e5e5e7; }" +
                ".test-results h3 { font-size: 1.1rem; font-weight: 600; color: #1d1d1f; margin-bottom: 15px; }" +
                ".test-item { padding: 8px 0; font-size: 0.85rem; border-bottom: 1px solid #f2f2f2; }" +
                ".test-item:last-child { border-bottom: none; }" +
                ".footer { background: #1d1d1f; color: #f5f5f7; padding: 20px 25px; text-align: center; }" +
                ".footer p { font-size: 0.8rem; color: #a1a1a6; margin: 4px 0; }" +
                ".footer .highlight { color: #007aff; font-weight: 600; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>⚡ Test Report</h1>" +
                "<p class='subtitle'>%s • %s • %.1f%% Success</p>" +
                "</div>" +
                "<div class='summary'>" +
                "<div class='metric'><div class='icon'>📋</div><h3>%d</h3><p>Total</p></div>" +
                "<div class='metric'><div class='icon'>✓</div><h3>%d</h3><p>Passed</p></div>" +
                "<div class='metric'><div class='icon'>✕</div><h3>%d</h3><p>Failed</p></div>" +
                "<div class='metric'><div class='icon'>⏸</div><h3>%d</h3><p>Skipped</p></div>" +
                "</div>" +
                "<div class='progress-section'>" +
                "<div class='progress-label'>Success Rate: %.1f%%</div>" +
                "<div class='progress-bar'><div class='progress-fill'></div></div>" +
                "</div>" +
                "<div class='details'>" +
                "<div class='detail-row'><span class='label'>Environment</span><span class='value'>Local</span></div>" +
                "<div class='detail-row'><span class='label'>Browser</span><span class='value'>Chrome</span></div>" +
                "<div class='detail-row'><span class='label'>Framework</span><span class='value'>JSD Automation</span></div>" +
                "</div>" +
                "<div class='test-results'>" +
                "<h3>📋 Test Results</h3>" +
                "%s" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Automated report by <span class='highlight'>JSD Framework</span></p>" +
                "<p>🔗 Built with precision by Nilesh Patil</p>" +
                "</div>" +
                "</div></body></html>",
                successRate, currentDate, executionTime, successRate,
                total, passed, failed, (total - passed - failed),
                successRate,
                buildTestResultsHtml()
            );
            
            // Send beautiful email directly
            EmailSender.sendSimpleEmail("JSD Test Automation Report - By Nilesh Patil", beautifulHtml);
            
            System.out.println("✅ SIMPLE EMAIL SENT SUCCESSFULLY!");
            
        } catch (Exception e) {
            System.out.println("❌ EMAIL FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String buildTestResultsHtml() {
        StringBuilder html = new StringBuilder();
        for (String testResult : testResults) {
            html.append("<div class='test-item'>").append(testResult).append("</div>");
        }
        return html.toString();
    }
}
