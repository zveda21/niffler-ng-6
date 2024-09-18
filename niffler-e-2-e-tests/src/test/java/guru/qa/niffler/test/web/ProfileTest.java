package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import com.codeborne.selenide.Configuration;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @Category(username = "duck",
            name = "byzi",//we can leave this string like this, but in the CategoryExtension class it is hard-coded and generates a random value
            archived = true)
    void archivedCategoryShouldBePresentInCategoriesList(CategoryJson category) {
        Configuration.browserSize = "1920x1080";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        new ProfilePage().clickOnPersonIcon()
                .clickOnProfileButton()
                .clickOnShowArchivedToggle()
                .checkArchivedCategoryName(category.name());
    }

    @Category(
            username = "duck",
            name = "car",
            archived = false
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Configuration.browserSize = "1920x1080";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        new ProfilePage().clickOnPersonIcon()
                .clickOnProfileButton()
                .checkActiveCategoriesIsVisible(category.name());
    }
}
