# JSD Test Automation Framework

This framework provides automated testing capabilities with Selenium WebDriver, TestNG, and Allure reporting.

## Automatic Allure Report Opening

After implementing the automatic report opening feature, you now have multiple ways to run tests and automatically open the Allure report:

### Method 1: Using TestNG Listener (Recommended)

The framework includes an `AllureReportListener` that automatically generates and opens the Allure report when test suites complete.

**How to run:**
```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

The listener will:
1. Generate the Allure report from test results
2. Automatically open the report in your default browser
3. Log the process for debugging

### Method 2: Using Batch Script

Run the provided batch script for a convenient one-click solution:

```bash
run-tests-with-report.bat
```

This script will:
1. Clean and run tests using Maven
2. Generate Allure report if tests pass
3. Open the report in your default browser
4. Provide status messages throughout the process

### Method 3: Using Maven Profile

Use the Maven profile for integrated report generation:

```bash
mvn clean test -Ptest-with-report -DsuiteXmlFile=testng.xml
mvn allure:generate
```

## Prerequisites

Make sure you have Allure installed on your system:

### Install Allure via npm:
```bash
npm install -g allure-commandline
```

### Install Allure via Scoop (Windows):
```bash
scoop install allure
```

### Verify Installation:
```bash
allure --version
```

## Manual Report Generation

If you prefer to generate reports manually:

```bash
# Generate report
allure generate allure-results -o allure-report --clean

# Serve report (opens automatically)
allure serve allure-results
```

## Project Structure

```
jsd-framework/
├── src/
│   ├── main/java/org/jsd/
│   │   ├── base/
│   │   ├── pages/
│   │   └── utils/
│   └── test/java/org/jsd/
│       ├── base/
│       ├── listeners/          # Contains AllureReportListener
│       ├── tests/
│       └── resources/
├── allure-results/             # Test execution results
├── allure-report/              # Generated HTML reports
├── testng.xml                  # TestNG configuration with listener
├── run-tests-with-report.bat   # Batch script for easy execution
└── pom.xml                     # Maven configuration
```

## Features

- **Parallel Test Execution**: Tests run in parallel across different browsers
- **Automatic Report Generation**: Reports are generated and opened automatically
- **Cross-browser Testing**: Support for Chrome, Edge, and other browsers
- **Detailed Logging**: Comprehensive logging with Log4j2
- **Screenshot Capture**: Automatic screenshot capture on test failures
- **Allure Integration**: Rich HTML reports with test history and analytics

## Troubleshooting

### Report Not Opening Automatically

1. **Check Allure Installation**: Ensure Allure is installed and available in PATH
2. **Browser Issues**: The system will try multiple methods to open the browser
3. **Manual Opening**: If automatic opening fails, navigate to `allure-report/index.html`

### Common Issues

- **Java Version**: Ensure you're using Java 21 as specified in pom.xml
- **Maven Dependencies**: Run `mvn clean install` to ensure all dependencies are downloaded
- **WebDriver Issues**: WebDriverManager handles driver downloads automatically

## Configuration

### Browser Configuration
Modify `testng.xml` to change browser parameters:
```xml
<parameter name="browser" value="chrome"/>  <!-- or "edge", "firefox" -->
```

### Parallel Execution
Adjust thread count in `testng.xml`:
```xml
<suite name="Parallel Browser Suite" parallel="tests" thread-count="2">
```

### Report Customization
Modify `AllureReportListener.java` to customize report generation behavior.
