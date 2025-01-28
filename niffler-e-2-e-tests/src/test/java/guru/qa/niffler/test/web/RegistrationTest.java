package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegistrationTest {

  private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

  @Test
  void shouldRegisterNewUser() {
    String newUsername = randomUsername();
    String password = "12345";
    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .doRegister()
        .fillRegisterPage(newUsername, password, password)
        .successSubmit()
        .fillLoginPage(newUsername, password)
            .submit(new MainPage(driver))
        .checkThatPageLoaded();
  }

  @Test
  void shouldNotRegisterUserWithExistingUsername() {
    String existingUsername = "duck";
    String password = "12345";

    driver.open(LoginPage.URL);
    new LoginPage(driver)
            .doRegister()
        .fillRegisterPage(existingUsername, password, password)
        .errorSubmit();
    new LoginPage(driver).checkError("Username `" + existingUsername + "` already exists");
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    String newUsername = randomUsername();
    String password = "12345";

    driver.open(LoginPage.URL);

    new LoginPage(driver).doRegister()
        .fillRegisterPage(newUsername, password, "bad password submit")
        .errorSubmit();
    new LoginPage(driver).checkError("Passwords should be equal");
  }
}
