package org.jsd.pages;

import org.jsd.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import io.qameta.allure.Step;

public class LoginPage extends BasePage {
    // Locators
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Enter username: {0}")
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    @Step("Enter password: {0}")
    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    @Step("Click login button")
    public void clickLogin() {
        click(loginButton);
    }

    @Step("Login with credentials: {0}/{1}")
    public void login(String username, String password) {
        enterUsername(username)
            .enterPassword(password)
            .clickLogin();
    }
}
