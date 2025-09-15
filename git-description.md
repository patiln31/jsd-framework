# JSD Test Automation Framework

A comprehensive Selenium WebDriver test automation framework with TestNG and Allure reporting for parallel cross-browser testing.

## Key Features

- **Parallel Test Execution** - Run tests simultaneously across multiple browsers
- **Automatic Allure Reporting** - Generate and serve interactive HTML reports with auto-termination
- **Cross-browser Support** - Chrome, Edge, Firefox with WebDriverManager
- **Page Object Model** - Clean, maintainable test structure
- **Built-in Utilities** - Screenshot capture, wait helpers, configuration management
- **Local Allure Integration** - Self-contained reporting without external dependencies

## Quick Start

```bash
# Run tests with automatic report
mvn clean test -DsuiteXmlFile=testng.xml

# Or use batch file
run-and-view-report.bat
```

## Tech Stack

- **Java 21** - Modern Java features
- **Selenium WebDriver** - Browser automation
- **TestNG** - Test framework with parallel execution
- **Allure** - Rich HTML reporting with history
- **Maven** - Dependency management and build automation
- **Log4j2** - Comprehensive logging

## Project Structure

- `src/main/java` - Page objects and utilities
- `src/test/java` - Test classes and listeners
- `allure-results/` - Test execution data
- `allure-report/` - Generated HTML reports
- `.allure/` - Local Allure installation

Perfect for teams needing reliable, scalable test automation with professional reporting.