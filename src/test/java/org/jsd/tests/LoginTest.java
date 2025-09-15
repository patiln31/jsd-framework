package org.jsd.tests;

import io.qameta.allure.*;
import org.jsd.base.BaseTest;
import org.jsd.pages.LoginPage;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@Epic("Authentication")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Test
    @Story("User Authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify user can login with valid credentials")
    public void testValidLogin() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        Thread.sleep(10000);
        loginPage.enterUsername("standard_user")
                .enterPassword("secret_sauce")
                .clickLogin();

        assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
            "User should be redirected to inventory page after login");
    }
}
