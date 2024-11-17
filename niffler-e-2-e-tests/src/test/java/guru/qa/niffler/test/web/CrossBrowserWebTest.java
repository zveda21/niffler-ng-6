package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.browser.Browser;
import guru.qa.niffler.browser.BrowserArgumentConverter;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@guru.qa.niffler.jupiter.annotation.CrossBrowserWebTest
public class CrossBrowserWebTest {

    @ParameterizedTest
    @EnumSource(Browser.class)
    void crossBrowserTest(@ConvertWith(BrowserArgumentConverter.class) SelenideDriver driver) {
        driver.open(LoginPage.URL);
        new LoginPage(driver)
                .fillLoginPage(randomUsername(), "BAD_PASSWORD")
                .submit(new LoginPage(driver));
    }
}
