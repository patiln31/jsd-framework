package org.jsd.listeners;

import org.jsd.utils.ConfigReader;
import org.jsd.utils.EmailSender;
import org.testng.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SuiteEmailListener implements ISuiteListener {
    private LocalDateTime suiteStartTime;
    private List<String> allTestResults = new ArrayList<>();
    private List<String> screenshotPaths = new ArrayList<>();
    private static boolean emailSent = false;

    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = LocalDateTime.now();
        emailSent = false; // Reset for new suite
        allTestResults.clear();
        screenshotPaths.clear();
        System.out.println("🚀 SUITE EMAIL LISTENER: Suite started - " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("📧 SUITE EMAIL LISTENER: Suite finished, preparing email...");
        
        // Check if email already sent
        if (emailSent) {
            System.out.println("📧 Email already sent for this suite, skipping...");
            return;
        }
        
        // Check if email notification is enabled
        boolean emailEnabled = Boolean.parseBoolean(ConfigReader.getProperty("email.notification.enabled", "false"));
        if (!emailEnabled) {
            System.out.println("📧 Email notification disabled");
            return;
        }

        // Collect all test results from the suite
        int totalTests = 0, passedTests = 0, failedTests = 0, skippedTests = 0;
        
        Map<String, ISuiteResult> results = suite.getResults();
        for (ISuiteResult result : results.values()) {
            ITestContext testContext = result.getTestContext();
            
            // Count results
            passedTests += testContext.getPassedTests().size();
            failedTests += testContext.getFailedTests().size();
            skippedTests += testContext.getSkippedTests().size();
            
            // Collect passed test details
            for (ITestResult testResult : testContext.getPassedTests().getAllResults()) {
                String testName = testResult.getMethod().getMethodName();
                String className = testResult.getTestClass().getName().substring(testResult.getTestClass().getName().lastIndexOf('.') + 1);
                long duration = testResult.getEndMillis() - testResult.getStartMillis();
                allTestResults.add(String.format("✅ %s.%s - PASSED (%dms)", className, testName, duration));
            }
            
            // Collect failed test details
            for (ITestResult testResult : testContext.getFailedTests().getAllResults()) {
                String testName = testResult.getMethod().getMethodName();
                String className = testResult.getTestClass().getName().substring(testResult.getTestClass().getName().lastIndexOf('.') + 1);
                String errorMsg = testResult.getThrowable() != null ? testResult.getThrowable().getMessage() : "Unknown error";
                long duration = testResult.getEndMillis() - testResult.getStartMillis();
                
                // Capture screenshot for failed test
                try {
                    Object testInstance = testResult.getInstance();
                    if (testInstance instanceof org.jsd.base.BaseTest) {
                        org.jsd.base.BaseTest baseTest = (org.jsd.base.BaseTest) testInstance;
                        if (baseTest.getDriver() != null) {
                            String screenshotPath = org.jsd.utils.ScreenshotUtils.captureScreenshot(baseTest.getDriver(), testName + "_FAILED");
                            if (screenshotPath != null) {
                                screenshotPaths.add(screenshotPath);
                                System.out.println("📸 Screenshot captured and added for attachment: " + testName);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("❌ Failed to capture screenshot: " + e.getMessage());
                }
                
                allTestResults.add(String.format("❌ %s.%s - FAILED (%dms) - %s 📸", className, testName, duration, errorMsg));
            }
            
            // Collect skipped test details
            for (ITestResult testResult : testContext.getSkippedTests().getAllResults()) {
                String testName = testResult.getMethod().getMethodName();
                String className = testResult.getTestClass().getName().substring(testResult.getTestClass().getName().lastIndexOf('.') + 1);
                allTestResults.add(String.format("⏭️ %s.%s - SKIPPED", className, testName));
            }
        }
        
        totalTests = passedTests + failedTests + skippedTests;
        
        System.out.println("📊 Suite Results: Total=" + totalTests + ", Passed=" + passedTests + ", Failed=" + failedTests + ", Skipped=" + skippedTests);
        
        // Send email with complete results
        sendSuiteEmail(totalTests, passedTests, failedTests, skippedTests);
        emailSent = true; // Mark as sent
    }

    private void sendSuiteEmail(int total, int passed, int failed, int skipped) {
        try {
            System.out.println("📧 SUITE EMAIL LISTENER: Sending complete suite email...");
            
            // Calculate execution time
            LocalDateTime endTime = LocalDateTime.now();
            long durationSeconds = java.time.Duration.between(suiteStartTime, endTime).getSeconds();
            String executionTime = String.format("%dm %ds", durationSeconds / 60, durationSeconds % 60);
            
            // Calculate success rate
            double successRate = total > 0 ? (double) passed / total * 100 : 0;
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            
            // Build test results HTML
            StringBuilder testResultsHtml = new StringBuilder();
            for (String testResult : allTestResults) {
                testResultsHtml.append("<div class='test-item'>").append(testResult).append("</div>");
            }
            
            // Use beautiful HTML template with complete suite data
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
                "<h1>⚡ Test Suite Report</h1>" +
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
                "<div class='detail-row'><span class='label'>Browser</span><span class='value'>Chrome/Edge</span></div>" +
                "<div class='detail-row'><span class='label'>Framework</span><span class='value'>JSD Automation</span></div>" +
                "</div>" +
                "<div class='test-results'>" +
                "<h3>📋 Complete Test Results</h3>" +
                "%s" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Automated report by <span class='highlight'>JSD Framework</span></p>" +
                "<p>🔗 Built with precision by Nilesh Patil</p>" +
                "</div>" +
                "</div></body></html>",
                successRate, currentDate, executionTime, successRate,
                total, passed, failed, skipped,
                successRate,
                testResultsHtml.toString()
            );
            
            // Send email with screenshot attachments
            if (!screenshotPaths.isEmpty()) {
                EmailSender.sendEmailWithAttachments("JSD Test Suite Report - By Nilesh Patil", beautifulHtml, screenshotPaths);
                System.out.println("📎 Sent email with " + screenshotPaths.size() + " screenshot attachments");
            } else {
                EmailSender.sendSimpleEmail("JSD Test Suite Report - By Nilesh Patil", beautifulHtml);
                System.out.println("📧 Sent email without attachments (no failed tests)");
            }
            
            System.out.println("✅ SUITE EMAIL SENT SUCCESSFULLY!");
            
        } catch (Exception e) {
            System.out.println("❌ SUITE EMAIL FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
