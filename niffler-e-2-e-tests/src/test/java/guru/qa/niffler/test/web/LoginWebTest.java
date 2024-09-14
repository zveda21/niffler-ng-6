package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    public void mainPageShouldBeDisplayedAfterSuccessLogin() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        new MainPage()
                .checkThatPageContainsRootContainer();
    }

    @Test
    public void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String badCredentialsMessage = "Bad credentials";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "123456")
                .checkIfErrorMessageDisplayed(badCredentialsMessage);
    }
}
