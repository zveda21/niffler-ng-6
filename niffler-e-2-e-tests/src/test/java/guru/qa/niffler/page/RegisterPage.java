package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage> {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitPasswordInput = $("#passwordSubmit");
    private final SelenideElement signUpButton = $("button.form__submit");
    private final SelenideElement errorMessage = $(".form__error");
    private final SelenideElement successRegistryMessage = $("[class^='form__paragraph']");

    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String password) {
        submitPasswordInput.setValue(password);
        return this;
    }

    public RegisterPage submitRegistration() {
        signUpButton.click();
        return this;
    }

    public void checkThatErrorMessageDisplayed(String error) {
        errorMessage.shouldHave(text(error));
    }

    public void checkThatSuccessRegistryMessageDisplayed(String successMessage) {
        successRegistryMessage.shouldHave(text(successMessage));
    }
}
