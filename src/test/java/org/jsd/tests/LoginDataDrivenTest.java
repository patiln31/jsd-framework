package org.jsd.tests;

import io.qameta.allure.*;
import org.jsd.base.BaseTest;
import org.jsd.pages.LoginPage;
import org.jsd.utils.CSVReader;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@Epic("Authentication")
@Feature("Data Driven Login")
public class LoginDataDrivenTest extends BaseTest {

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
        
        Allure.parameter("Username", username);
        Allure.parameter("Password", password);
        Allure.parameter("Expected Result", expected);
        
        loginPage.enterUsername(username)
                .enterPassword(password)
                .clickLogin();

        switch (expected.toLowerCase()) {
            case "success" -> {
                assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                    "User should be redirected to inventory page after successful login");
                Allure.step("Login successful - redirected to inventory page");
            }
            case "locked" -> {
                // Deliberately failing this case to test screenshot capture functionality
                // This will trigger the ScreenshotListener to capture and attach screenshot
                assertTrue(loginPage.isErrorMessageDisplayed(),
                    "Error message should be displayed for locked user");
                
                // INTENTIONAL FAILURE: This assertion will fail to test screenshot capture
                // Comment out the line below to make the test pass normally
                assertFalse(loginPage.isErrorMessageDisplayed(), 
                    "INTENTIONAL FAILURE: Testing screenshot capture for locked user scenario");
                
                assertTrue(loginPage.getErrorMessage().contains("locked"),
                    "Error message should mention user is locked");
                Allure.step("Login blocked - user account is locked (INTENTIONAL FAILURE FOR SCREENSHOT TEST)");
            }
            case "error" -> {
                assertTrue(loginPage.isErrorMessageDisplayed(),
                    "Error message should be displayed for invalid credentials");
                Allure.step("Login failed - invalid credentials");
            }
        }
    }
}