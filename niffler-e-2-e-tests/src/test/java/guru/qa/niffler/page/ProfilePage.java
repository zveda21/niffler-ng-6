package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class ProfilePage {

    public static String profilePageUrl = Config.getInstance().frontUrl() + "profile";

    private final SelenideElement personIcon = $("[data-testid='PersonIcon']");
    private final SelenideElement profileButton = $("[href='/profile']");
    private final SelenideElement showArchivedToggle = $("[class^='MuiFormControlLabel-root']");
    private final ElementsCollection archivedCategoriesList = $$("[class*='clickableColorDefault'] [class^='MuiChip-label']");
    private final ElementsCollection activeCategoriesList = $$("[class*='clickableColorPrimary'] [class^='MuiChip-label']");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangesButton = $("[class^='MuiGrid-root'] button[class*='containedPrimary']");

    public ProfilePage clickOnPersonIcon() {
        personIcon.click();
        return this;
    }

    public ProfilePage clickOnProfileButton() {
        profileButton.click();
        return this;
    }

    public ProfilePage clickOnShowArchivedToggle() {
        showArchivedToggle.click();
        return this;
    }

    public void checkArchivedCategoryName(String categoryName) {
        archivedCategoriesList.find(text(categoryName)).shouldBe(visible);
    }

    public void checkActiveCategoriesIsVisible(String categoryName) {
        activeCategoriesList.find(text(categoryName)).shouldBe(visible);
    }

    public ProfilePage setName(String username){
        nameInput.setValue(username);
        return this;
    }

    public ProfilePage clickOnSaveChangesButton() {
        saveChangesButton.click();
        return this;
    }
}
