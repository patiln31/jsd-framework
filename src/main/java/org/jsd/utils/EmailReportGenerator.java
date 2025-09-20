package org.jsd.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailReportGenerator {
    private static final Logger log = LogManager.getLogger(EmailReportGenerator.class);
    
    public static class FailedTestScreenshot {
        public String testName;
        public String base64Screenshot;
        public String timestamp;
        
        public FailedTestScreenshot(String testName, String base64Screenshot, String timestamp) {
            this.testName = testName;
            this.base64Screenshot = base64Screenshot;
            this.timestamp = timestamp;
        }
    }
    
    public static String generateHTMLReport(int totalTests, int passed, int failed, int skipped, 
                                          String executionTime, String environment, String browser, 
                                          java.util.List<FailedTestScreenshot> screenshots) {
        
        String htmlTemplate = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Test Execution Report</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', Helvetica, Arial, sans-serif; background: #f5f5f7; line-height: 1.47059; font-weight: 400; letter-spacing: -0.022em; min-height: 100vh; padding: 20px; }
                    .container { max-width: 1200px; margin: 0 auto; background: #ffffff; border-radius: 18px; box-shadow: 0 4px 60px rgba(0, 0, 0, 0.07); overflow: hidden; }
                    .header { background: linear-gradient(180deg, #ffffff 0%%, #f5f5f7 100%%); padding: 60px 40px 40px; text-align: center; position: relative; border-bottom: 1px solid #d2d2d7; }
                    .header h1 { font-size: 3rem; font-weight: 700; color: #1d1d1f; margin-bottom: 12px; letter-spacing: -0.04em; }
                    .header .subtitle { font-size: 1.25rem; color: #6e6e73; font-weight: 400; margin-bottom: 30px; }
                    .watermark { position: absolute; bottom: -10px; left: 50%%; transform: translateX(-50%%); background: linear-gradient(135deg, #007aff 0%%, #5856d6 50%%, #af52de 100%%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; font-size: 1.1rem; font-weight: 700; letter-spacing: 0.5px; text-transform: uppercase; }
                    .watermark::before { content: '✨ Crafted by '; background: linear-gradient(135deg, #ff9500 0%%, #ff6b6b 100%%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
                    .watermark::after { content: ' ✨'; background: linear-gradient(135deg, #ff9500 0%%, #ff6b6b 100%%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
                    .summary { display: grid; grid-template-columns: repeat(4, 1fr); padding: 40px; gap: 20px; }
                    .metric { background: #ffffff; padding: 32px 24px; border-radius: 12px; text-align: center; border: 1px solid #f2f2f2; }
                    .metric .icon { width: 44px; height: 44px; border-radius: 10px; margin: 0 auto 16px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
                    .total .icon { background: linear-gradient(135deg, #007aff, #5856d6); }
                    .passed .icon { background: linear-gradient(135deg, #34c759, #30d158); }
                    .failed .icon { background: linear-gradient(135deg, #ff3b30, #ff6b6b); }
                    .skipped .icon { background: linear-gradient(135deg, #ff9500, #ffb340); }
                    .metric h3 { font-size: 2.5rem; font-weight: 700; color: #1d1d1f; margin-bottom: 8px; letter-spacing: -0.04em; }
                    .metric p { font-size: 1rem; color: #86868b; font-weight: 600; text-transform: uppercase; letter-spacing: 0.06em; }
                    .progress-section { padding: 0 40px 40px; }
                    .progress-label { font-size: 1.1rem; font-weight: 600; color: #1d1d1f; margin-bottom: 12px; text-align: center; }
                    .progress-bar { height: 8px; background: #f2f2f2; border-radius: 4px; overflow: hidden; position: relative; }
                    .progress-fill { height: 100%%; background: linear-gradient(90deg, #34c759, #30d158); width: %s%%; border-radius: 4px; }
                    .info-section { padding: 40px; background: #fbfbfd; border-top: 1px solid #f2f2f2; }
                    .info-section h2 { font-size: 1.75rem; font-weight: 700; color: #1d1d1f; margin-bottom: 32px; text-align: center; letter-spacing: -0.022em; }
                    .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px; }
                    .info-card { background: #ffffff; padding: 24px; border-radius: 12px; border: 1px solid #f2f2f2; }
                    .info-card .label { font-size: 0.85rem; color: #86868b; font-weight: 600; text-transform: uppercase; letter-spacing: 0.06em; margin-bottom: 8px; }
                    .info-card .value { font-size: 1.25rem; color: #1d1d1f; font-weight: 600; letter-spacing: -0.022em; }
                    .footer { background: #1d1d1f; color: #f5f5f7; padding: 40px; text-align: center; }
                    .footer p { margin: 8px 0; font-size: 0.95rem; color: #a1a1a6; font-weight: 400; }
                    .footer .highlight { color: #007aff; font-weight: 600; }
                    .screenshots-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
                    .screenshot-card { background: #ffffff; padding: 20px; border-radius: 12px; border: 1px solid #f2f2f2; text-align: center; }
                    .screenshot-header h4 { color: #1d1d1f; margin-bottom: 8px; font-size: 1.1rem; }
                    .screenshot-header .timestamp { color: #86868b; font-size: 0.9rem; }
                    @media (max-width: 768px) { .summary { grid-template-columns: repeat(2, 1fr); padding: 30px 20px; } .info-grid { grid-template-columns: 1fr; } .screenshots-grid { grid-template-columns: 1fr; } }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Test Execution Report</h1>
                        <p class="subtitle">Comprehensive automation results with detailed insights</p>
                        <div style="margin: 20px 0; padding: 15px; background: linear-gradient(135deg, #007aff, #5856d6); border-radius: 12px;">
                            <a href="%s" target="_blank" style="color: white; text-decoration: none; font-weight: 600; font-size: 1.1rem;">
                                📊 View Detailed HTML Report (Click to Open)
                            </a>
                        </div>
                        <div class="watermark">Nilesh Patil</div>
                    </div>
                    
                    <div class="summary">
                        <div class="metric total">
                            <div class="icon">📊</div>
                            <h3>%d</h3>
                            <p>Total Tests</p>
                        </div>
                        <div class="metric passed">
                            <div class="icon">✅</div>
                            <h3>%d</h3>
                            <p>Passed</p>
                        </div>
                        <div class="metric failed">
                            <div class="icon">❌</div>
                            <h3>%d</h3>
                            <p>Failed</p>
                        </div>
                        <div class="metric skipped">
                            <div class="icon">⏭️</div>
                            <h3>%d</h3>
                            <p>Skipped</p>
                        </div>
                    </div>
                    
                    <div class="progress-section">
                        <div class="progress-label">Success Rate: %.1f%%</div>
                        <div class="progress-bar">
                            <div class="progress-fill"></div>
                        </div>
                    </div>
                    
                    <div class="info-section">
                        <h2>Execution Environment</h2>
                        <div class="info-grid">
                            <div class="info-card">
                                <div class="label">Environment</div>
                                <div class="value">%s</div>
                            </div>
                            <div class="info-card">
                                <div class="label">Browser</div>
                                <div class="value">%s</div>
                            </div>
                            <div class="info-card">
                                <div class="label">Duration</div>
                                <div class="value">%s</div>
                            </div>
                            <div class="info-card">
                                <div class="label">Success Rate</div>
                                <div class="value">%.1f%%</div>
                            </div>
                            <div class="info-card">
                                <div class="label">Completed</div>
                                <div class="value">%s</div>
                            </div>
                        </div>
                    </div>
                    
                    %s
                    
                    <div class="footer">
                        <p>Automated testing powered by <span class="highlight">JSD Framework</span> - By Nilesh Patil</p>
                        <p>Built with precision and attention to detail</p>
                    </div>
                </div>
            </body>
            </html>
            """;
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        double successRate = totalTests > 0 ? Math.min((double) passed / totalTests * 100, 100.0) : 0;
        
        // Generate screenshots section
        String screenshotsSection = generateScreenshotsSection(screenshots);
        
        // Generate report file path for link (will be updated after saving)
        String reportLink = "#"; // Placeholder, will be updated after saving
        
        String htmlContent = String.format(htmlTemplate, 
            reportLink, String.format("%.1f", successRate), totalTests, passed, failed, skipped, successRate,
            environment, browser, executionTime, successRate, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            screenshotsSection);
        
        // Save HTML report to file and get the actual path
        String actualReportPath = saveHTMLReport(htmlContent);
        
        // Update the link in HTML content with actual file path
        if (actualReportPath != null) {
            htmlContent = htmlContent.replace("#", "file://" + actualReportPath.replace("\\", "/"));
        }
        
        return htmlContent;
    }
    
    private static String saveHTMLReport(String htmlContent) {
        try {
            Path reportDir = Paths.get(System.getProperty("user.dir"), "test-output", "email-reports");
            if (!Files.exists(reportDir)) {
                Files.createDirectories(reportDir);
            }
            
            String fileName = "test-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".html";
            Path reportFile = reportDir.resolve(fileName);
            
            Files.write(reportFile, htmlContent.getBytes());
            log.info("HTML email report saved to: {}", reportFile.toString());
            
            return reportFile.toAbsolutePath().toString();
            
        } catch (IOException e) {
            log.error("Failed to save HTML report: {}", e.getMessage());
            return null;
        }
    }
    
    private static String generateScreenshotsSection(java.util.List<FailedTestScreenshot> screenshots) {
        if (screenshots == null || screenshots.isEmpty()) {
            return "";
        }
        
        StringBuilder section = new StringBuilder();
        section.append("<div class=\"info-section\">\n");
        section.append("<h2>Failed Test Screenshots</h2>\n");
        section.append("<div class=\"screenshots-grid\">\n");
        
        for (FailedTestScreenshot screenshot : screenshots) {
            section.append(String.format(
                "<div class=\"screenshot-card\">\n" +
                "<div class=\"screenshot-header\">\n" +
                "<h4>%s</h4>\n" +
                "<span class=\"timestamp\">Failed at %s</span>\n" +
                "</div>\n" +
                "<div style=\"background: #f8f9fa; padding: 15px; border-radius: 8px; text-align: center;\">\n" +
                "<p style=\"color: #666; margin-bottom: 10px;\">📸 Screenshot captured for test failure</p>\n" +
                "<div style=\"background: #ff3b30; color: white; padding: 10px; border-radius: 6px; display: inline-block;\">\n" +
                "<strong>❌ Test Failed - Screenshot Available</strong>\n" +
                "</div>\n" +
                "<p style=\"font-size: 12px; color: #999; margin-top: 8px;\">Click 'View Detailed HTML Report' above to see screenshots</p>\n" +
                "</div>\n" +
                "</div>\n",
                screenshot.testName, screenshot.timestamp
            ));
        }
        
        section.append("</div>\n");
        section.append("</div>\n");
        
        return section.toString();
    }
}