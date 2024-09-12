package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement createNewAccountButton = $(".form__register");
    private final SelenideElement errorMessage = $(".form__error-container");

    public LoginPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return this;
    }

    // Method to navigate to the registration page
    public RegisterPage clickOnCreateNewAccountButton() {
        createNewAccountButton.click();
        return new RegisterPage();
    }

    // Method to check if an error message is displayed
    public void checkIfErrorMessageDisplayed(String message) {
        errorMessage.shouldHave(text(message));
    }
}
