package org.jsd.tests;

import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsd.base.BaseTest;
import org.jsd.pages.LoginPage;
import org.jsd.utils.CSVReader;
import org.jsd.utils.CommonActions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@Epic("Authentication")
@Feature("Data Driven Login")
public class LoginDataDrivenTest extends BaseTest {
    private static final Logger log = LogManager.getLogger(LoginDataDrivenTest.class);

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        String filePath = getClass().getClassLoader().getResource("testdata/login_data.csv").getPath();
        return CSVReader.readCSVData(filePath);
    }

    @Test(dataProvider = "loginData")
    @Story("User Authentication with Multiple Credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify login functionality with different user credentials from Excel data")
    public void testLoginWithMultipleUsers(String username, String password, String expected) {
        LoginPage loginPage = new LoginPage(driver);
        CommonActions actions = new CommonActions(driver);
        
        Allure.parameter("Username", username);
        Allure.parameter("Password", password);
        Allure.parameter("Expected Result", expected);
        
        loginPage.enterUsername(username)
                .enterPassword(password)
                .clickLogin();

        switch (expected.toLowerCase()) {
            case "success" -> {
                actions.verifyUrlContains("/inventory.html");
                Allure.step("Login successful - redirected to inventory page");
            }
            case "locked" -> {
                assertTrue(loginPage.isErrorMessageDisplayed(),
                    "Error message should be displayed for locked user");
                
                // Capture screenshot before intentional failure
                captureScreenshotForAllure("Before Intentional Failure");
                
                // INTENTIONAL FAILURE: Keep this to test screenshot capture functionality
                assertFalse(loginPage.isErrorMessageDisplayed(), 
                    "INTENTIONAL FAILURE: Testing screenshot capture for locked user scenario");
                
                assertTrue(loginPage.getErrorMessage().contains("locked"),
                    "Error message should mention user is locked");
                Allure.step("Login blocked - user account is locked");
            }
            case "error" -> {
                actions.verifyElementDisplayed(org.openqa.selenium.By.cssSelector("[data-test='error']"));
                Allure.step("Login failed - invalid credentials");
            }
        }
    }
    
    @Attachment(value = "{screenshotName}", type = "image/png")
    private byte[] captureScreenshotForAllure(String screenshotName) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            return screenshot;
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
            return new byte[0];
        }
    }

}