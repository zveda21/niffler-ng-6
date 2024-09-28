package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "duck",
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldBePresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(category.username(), "12345");
        new ProfilePage().clickOnPersonIcon()
                .clickOnProfileButton()
                .clickOnShowArchivedToggle()
                .checkArchivedCategoryName(category.name());
    }

    @User(
            username = "duck",
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(category.username(), "12345");
        new ProfilePage().clickOnPersonIcon()
                .clickOnProfileButton()
                .checkActiveCategoriesIsVisible(category.name());
    }
}
