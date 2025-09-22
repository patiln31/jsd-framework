# JSD Test Automation Framework

## 🎥 Framework Demo Video

<iframe src="https://player.vimeo.com/video/1120765938?badge=0&amp;autopause=0&amp;player_id=0&amp;app_id=58479" width="800" height="450" frameborder="0" allow="autoplay; fullscreen; picture-in-picture; clipboard-write; encrypted-media; web-share" referrerpolicy="strict-origin-when-cross-origin" title="JSD framework video"></iframe>

---

Test automation framework built with Selenium WebDriver, TestNG, Allure reporting, Docker Grid support, visual testing, and automatic screenshot capture on failures.

## 🚀 Tech Stack & Versions

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming Language |
| **Maven** | 3.6+ | Build & Dependency Management |
| **Selenium WebDriver** | 4.15.0 | Browser Automation |
| **TestNG** | 7.8.0 | Testing Framework |
| **Allure** | 2.29.0 | Test Reporting |
| **WebDriverManager** | 5.6.2 | Driver Management |
| **Log4j2** | 2.22.0 | Logging |
| **Apache POI** | 5.2.4 | Excel Data Handling |
| **Jackson** | 2.15.3 | JSON Processing |

## 📋 Prerequisites

### System Requirements
- **Java 21** or higher
- **Maven 3.6+**
- **Node.js** (for Allure CLI)
- **Git**
- **Chrome/Edge/Firefox** browsers

### Installation Steps

#### 1. Install Java 21

**Windows:**
```cmd
# Download from Oracle JDK or use package manager
winget install Oracle.JDK.21
# Or download from: https://www.oracle.com/java/technologies/downloads/
```

**macOS:**
```bash
# Using Homebrew
brew install openjdk@21
# Or download from Oracle website
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

**Verify installation (All OS):**
```bash
java -version
```

#### 2. Install Maven

**Windows:**
```cmd
# Using Chocolatey
choco install maven
# Or download from: https://maven.apache.org/download.cgi
```

**macOS:**
```bash
# Using Homebrew
brew install maven
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install maven
```

**Verify installation (All OS):**
```bash
mvn -version
```

#### 3. Install Allure CLI

**All OS (via npm - Recommended):**
```bash
npm install -g allure-commandline
```

**Windows (via Scoop):**
```cmd
scoop install allure
```

**macOS (via Homebrew):**
```bash
brew install allure
```

**Linux (Manual Installation):**
```bash
# Download and extract Allure
wget https://github.com/allure-framework/allure2/releases/download/2.29.0/allure-2.29.0.tgz
tar -zxvf allure-2.29.0.tgz
sudo mv allure-2.29.0 /opt/allure
echo 'export PATH="/opt/allure/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

**Verify installation (All OS):**
```bash
allure --version
```

#### 4. Clone Repository
```bash
git clone <repository-url>
cd jsd-framework
```

## 🏃‍♂️ First Run - Quick Start

### Step 1: Verify Setup
```bash
# Check all prerequisites
java -version
mvn -version
allure --version
```

### Step 2: Run Tests

**Windows:**
```cmd
# Option 1: Use provided script
run-tests.bat

# Option 2: Maven command
mvn clean test -DsuiteXmlFile=testng.xml
```

**macOS/Linux:**
```bash
# Option 1: Make script executable and run
chmod +x run-tests.sh
./run-tests.sh

# Option 2: Maven command
mvn clean test -DsuiteXmlFile=testng.xml
```

**All OS (Maven commands):**
```bash
# Run specific test
mvn clean test -Dtest=LoginDataDrivenTest

# Run with specific browser
mvn clean test -Dbrowser=chrome
```

### Step 3: View Reports
- **Allure report opens automatically** after test execution
- **Manual report generation**: `allure serve allure-results`

## 📁 Project Structure

```
jsd-framework/
├── src/
│   ├── main/java/org/jsd/
│   │   ├── base/                    # Base classes
│   │   │   ├── BasePage.java        # Common page operations
│   │   │   └── DriverFactory.java   # WebDriver management
│   │   ├── pages/                   # Page Object Model
│   │   │   └── LoginPage.java       # Login page implementation
│   │   └── utils/                   # Utility classes
│   │       ├── CSVReader.java       # CSV data reader
│   │       ├── ExcelReader.java     # Excel data reader
│   │       └── TestDataUtils.java   # Test data utilities
│   └── test/java/org/jsd/
│       ├── base/
│       │   └── BaseTest.java        # Test base class
│       ├── listeners/               # TestNG listeners
│       │   ├── AllureReportListener.java    # Screenshot capture
│       │   ├── SuiteEmailListener.java      # Email notifications
│       │   ├── TestCleanupListener.java     # Cleanup operations
│       │   ├── EnvironmentListener.java     # Environment setup
│       │   ├── RetryAnalyzer.java           # Retry failed tests
│       │   └── RetryListener.java           # Retry configuration
│       ├── tests/                   # Test classes
│       │   ├── LoginTest.java       # Basic login tests
│       │   └── LoginDataDrivenTest.java     # Data-driven tests
│       ├── utils/                   # Test-specific utilities
│       │   ├── CommonActions.java   # Common test actions
│       │   ├── CustomAssertions.java # Enhanced assertions
│       │   └── VisualTestingUtils.java # Screenshot comparison
│       └── resources/
│           ├── configs/
│           │   └── config.properties        # Configuration
│           ├── testdata/
│           │   ├── login_data.csv           # CSV test data
│           │   └── login_data.xlsx          # Excel test data
│           └── log4j2.xml                   # Logging configuration
├── testng.xml                       # TestNG suite configuration
├── pom.xml                         # Maven dependencies
├── docker-compose.yml              # Docker setup
├── Jenkinsfile                     # CI/CD pipeline
└── run-tests.bat                   # Test execution script
```

## ✍️ Creating Your First Test Case

### Step 1: Create Page Object
```java
// src/main/java/org/jsd/pages/YourPage.java
package org.jsd.pages;

import org.jsd.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class YourPage extends BasePage {
    
    // Locators
    private final By elementLocator = By.id("your-element");
    
    public YourPage(WebDriver driver) {
        super(driver);
    }
    
    public YourPage performAction() {
        click(elementLocator);
        return this;
    }
    
    public boolean isElementVisible() {
        return isElementDisplayed(elementLocator);
    }
}
```

### Step 2: Create Test Class
```java
// src/test/java/org/jsd/tests/YourTest.java
package org.jsd.tests;

import io.qameta.allure.*;
import org.jsd.base.BaseTest;
import org.jsd.pages.YourPage;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@Epic("Your Epic Name")
@Feature("Your Feature Name")
public class YourTest extends BaseTest {
    
    @Test
    @Story("Your Story Name")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test description")
    public void testYourFunctionality() {
        YourPage yourPage = new YourPage(driver);
        
        Allure.step("Navigate to page", () -> {
            driver.get("https://your-url.com");
        });
        
        Allure.step("Perform action", () -> {
            yourPage.performAction();
        });
        
        Allure.step("Verify result", () -> {
            assertTrue(yourPage.isElementVisible(), "Element should be visible");
        });
    }
}
```

### Step 3: Add Test Data (Optional)
```csv
# src/test/resources/testdata/your_data.csv
Input1,Input2,Expected
value1,value2,result1
value3,value4,result2
```

### Step 4: Update TestNG Suite
```xml
<!-- Add to testng.xml -->
<test name="Your Test">
    <classes>
        <class name="org.jsd.tests.YourTest"/>
    </classes>
</test>
```

### Step 5: Run Your Test
```bash
mvn clean test -Dtest=YourTest
```

## 🐳 Docker Setup

### Prerequisites
- **Docker Desktop** installed and running
- **Docker Compose** available

### Step 1: Start Selenium Grid

**Windows:**
```cmd
# Use provided script
start-grid.bat

# Or manually
docker-compose up -d
```

**macOS/Linux:**
```bash
# Make script executable and run
chmod +x start-grid.sh
./start-grid.sh

# Or manually
docker-compose up -d
```

### Step 2: Verify Grid
- Open http://localhost:4444/ui
- Verify nodes are connected

### Step 3: Run Tests on Grid
```bash
# Run tests against Docker Grid
run-grid-tests.bat

# Or manually
mvn clean test -DsuiteXmlFile=testng.xml -Dgrid.url=http://localhost:4444 -Dexecution.env=Docker-Grid
```

### Step 4: Stop Grid
```bash
stop-grid.bat

# Or manually
docker-compose down
```

### Docker Configuration
```yaml
# docker-compose.yml structure
version: '3.8'
services:
  selenium-hub:
    image: selenium/hub:4.15.0
    ports:
      - "4444:4444"
  
  chrome:
    image: selenium/node-chrome:4.15.0
    depends_on:
      - selenium-hub
    environment:
      - HUB_HOST=selenium-hub
  
  firefox:
    image: selenium/node-firefox:4.15.0
    depends_on:
      - selenium-hub
    environment:
      - HUB_HOST=selenium-hub
```

## 📚 Framework Architecture

### Directory Structure & Dependency Scopes

The framework follows Maven's standard directory structure with specific considerations for dependency scopes:

#### Source Directories:
- **`src/main/java`** - Production code that doesn't depend on test frameworks
  - Base classes, Page Objects, Core utilities
  - Can only use dependencies with `compile` or `provided` scope
  
- **`src/test/java`** - Test code and test-specific utilities
  - Test classes, Listeners, Test utilities (CustomAssertions, VisualTestingUtils)
  - Can access all dependencies including `test` scoped ones (TestNG, etc.)

#### Key Dependencies & Scopes:
- **TestNG** - `<scope>test</scope>` - Only available in test directory
- **Selenium** - `compile` scope - Available in both main and test
- **Allure** - `compile` scope - Available in both main and test
- **Log4j2** - `compile` scope - Available in both main and test

#### Important Notes:
- Test-specific utilities (CustomAssertions, VisualTestingUtils) are in `src/test/java` because they use TestNG
- Page Objects and core utilities are in `src/main/java` for reusability
- This separation ensures clean architecture and proper dependency management

## ⚙️ Configuration

### Browser Configuration
```xml
<!-- testng.xml -->
<parameter name="browser" value="chrome"/>  <!-- chrome, edge, firefox -->
```

### Parallel Execution
```xml
<suite name="Test Suite" parallel="tests" thread-count="3">
```

### Email Notifications

#### Option 1: Using Configuration File
```properties
# src/test/resources/configs/config.properties
email.enabled=true
email.smtp.host=smtp.gmail.com
email.smtp.port=587
email.username=your-email@gmail.com
email.password=your-app-password
```

#### Option 2: Using Environment Variables (Recommended for Security)

**Windows (Command Prompt):**
```cmd
set EMAIL_USERNAME=your-email@gmail.com
set EMAIL_PASSWORD=your-app-password
mvn clean test -Demail.notification.enabled=true
```

**Windows (PowerShell):**
```powershell
$env:EMAIL_USERNAME="your-email@gmail.com"
$env:EMAIL_PASSWORD="your-app-password"
mvn clean test -Demail.notification.enabled=true
```

**macOS/Linux:**
```bash
export EMAIL_USERNAME=your-email@gmail.com
export EMAIL_PASSWORD=your-app-password
mvn clean test -Demail.notification.enabled=true
```

#### Option 3: Command Line Parameters
```bash
# Pass credentials via command line
mvn clean test -Demail.notification.enabled=true -Demail.username=your-email@gmail.com -Demail.password=your-app-password
```

#### Gmail App Password Setup (Required for Gmail)
1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password**:
   - Go to Google Account Settings
   - Security → 2-Step Verification → App passwords
   - Select "Mail" and generate password
   - Use this generated password (not your regular Gmail password)
3. **Use the 16-character app password** in any of the above methods

#### Disable Email Notifications
```bash
# Run tests without email notifications
mvn clean test -Demail.notification.enabled=false
```

## 🎯 Key Features

### Automatic Screenshot Capture
- **On test failures** - Screenshots automatically attached to Allure reports
- **High-quality PNG format** - Clear failure evidence
- **Integrated with Allure** - No manual intervention needed

### Visual Testing
- **Screenshot comparison** - Compare current screenshots with baseline images
- **Automatic diff generation** - Highlights differences in red
- **Baseline management** - Create and update baseline images
- **Allure integration** - Visual test results with baseline/actual/diff attachments

### Retry Mechanism
- **Configurable retry** - Retry failed tests automatically (configurable: true/false)
- **Smart retry logic** - Prevents infinite loops with max retry count
- **Detailed logging** - Track retry attempts and failures
- **System property override** - Control retry behavior via command line

### Custom Assertions
- **Enhanced assertions** - Element visibility, text validation, URL checks
- **Allure step integration** - Each assertion becomes an Allure step
- **Smart logging** - Visual indicators (✅/❌) for assertion results
- **Soft assert support** - Continue test execution after assertion failures

### Advanced Logging
- **Log4j2 configuration** - Console, file, and rolling file appenders
- **Automatic log rotation** - 10MB files with 5 backup copies
- **Separate log levels** - Framework, Selenium, and TestNG logging
- **Structured logging** - Logs saved to `test-output/logs/`

### Data-Driven Testing
- **CSV support** - Simple comma-separated data
- **Excel support** - Complex data structures
- **Dynamic data providers** - Flexible test parameterization

### Cross-Browser Testing
- **Chrome, Edge, Firefox** support
- **WebDriverManager** - Automatic driver management
- **Docker Grid** - Scalable execution

### Rich Reporting
- **Allure Reports** - Interactive HTML reports
- **Test history** - Track test trends
- **Email notifications** - Automated result sharing

## 🚀 Advanced Usage

### Running Specific Tests
```bash
# Single test class
mvn test -Dtest=LoginTest

# Single test method
mvn test -Dtest=LoginTest#testValidLogin

# Multiple test classes
mvn test -Dtest=LoginTest,LoginDataDrivenTest

# By groups
mvn test -Dgroups=smoke
```

### Environment Variables
```bash
# Set browser
mvn test -Dbrowser=firefox

# Set environment
mvn test -Denvironment=staging

# Enable email
mvn test -Demail.notification.enabled=true

# Disable retry mechanism
mvn test -Dretry.failed.tests=false

# Set max retry count
mvn test -Dmax.retry.count=3
```

### Using New Framework Features

#### Visual Testing
```java
// Compare screenshot with baseline
VisualTestingUtils.compareScreenshot(driver, "login-page");

// Update baseline image
VisualTestingUtils.updateBaseline(driver, "homepage");
```

#### Custom Assertions
```java
// Enhanced assertions with Allure steps
CustomAssertions.assertElementDisplayed(loginButton, "Login Button");
CustomAssertions.assertUrlContains(driver, "/dashboard");
CustomAssertions.assertTextEquals(actualText, expectedText, "Page Title");
```

#### Retry Configuration
```properties
# In config.properties
retry.failed.tests=true
max.retry.count=2

# Or via command line
mvn test -Dretry.failed.tests=false
```

## 🔧 Troubleshooting

### Common Issues

1. **Java Version Mismatch**
   ```bash
   # Check Java version
   java -version
   # Should be Java 21+
   ```

2. **Maven Not Found**
   
   **Windows:**
   ```cmd
   # Add Maven to PATH in System Environment Variables
   # Or reinstall using package manager
   choco install maven
   ```
   
   **macOS/Linux:**
   ```bash
   # Add Maven to PATH
   export PATH=$PATH:/path/to/maven/bin
   # Add to ~/.bashrc or ~/.zshrc for persistence
   ```

3. **Allure Command Not Found**
   ```bash
   # Install Allure CLI
   npm install -g allure-commandline
   ```

4. **Browser Driver Issues**
   - Framework uses WebDriverManager - drivers download automatically
   - Ensure internet connection for first run

5. **Port Already in Use (Docker)**
   
   **Windows:**
   ```cmd
   # Check what's using port 4444
   netstat -ano | findstr :4444
   # Kill process or change port in docker-compose.yml
   ```
   
   **macOS/Linux:**
   ```bash
   # Check what's using port 4444
   lsof -i :4444
   # Or use netstat
   netstat -tulpn | grep :4444
   # Kill process or change port in docker-compose.yml
   ```

## 📞 Support

For issues and questions:
1. Check this README
2. Review logs in `target/surefire-reports/`
3. Check Allure reports for detailed failure information

## 🎉 Happy Testing!

Your test automation framework is ready to use. Start by running the sample tests and then create your own following the patterns established in this framework.