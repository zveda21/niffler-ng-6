package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegistrationWebTest {

    private static final Config CFG = Config.getInstance();
    private final Faker faker = new Faker();

    @Test
    public void shouldRegisterNewUser() {
        String username = faker.name().username();
        String password = faker.internet().password(3, 12);
        String registrationSuccessMessage = "Congratulations! You've registered!";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickOnCreateNewAccountButton()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkThatSuccessRegistryMessageDisplayed(registrationSuccessMessage);

    }

    @Test
    public void shouldNotRegisterUserWithExistingUsername() {
        String username = "existinguser";
        String password = faker.internet().password(3, 12);
        String existingErrorMessage = "Username " + "`" + username + "` already exists";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickOnCreateNewAccountButton()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkThatErrorMessageDisplayed(existingErrorMessage);

    }

    @Test
    public void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = faker.name().username();
        String password = faker.internet().password(3, 12);
        String confirmPassword = faker.internet().password(4,12);
        String differentPasswordErrorMessage = "Passwords should be equal";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickOnCreateNewAccountButton()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(confirmPassword)
                .submitRegistration()
                .checkThatErrorMessageDisplayed(differentPasswordErrorMessage);

    }
}
