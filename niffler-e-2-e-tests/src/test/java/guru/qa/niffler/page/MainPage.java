package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage>{

    public static String url = Config.getInstance().frontUrl() + "main";

    private final SelenideElement personIcon = $("[data-testid='PersonIcon']");
    private final SelenideElement friendButton = $("li [href='/people/friends']");
    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement mainPageRootContainer = $("[class^='MuiBox-root']");
    private final SelenideElement signOutButton  = $(".form__error-container");

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).should(visible);
    }

    public void checkThatPageContainsRootContainer() {
        mainPageRootContainer.shouldBe(visible);
    }

    public FriendsPage clickOnFriendButtonUnderPersonIcon() {
        personIcon.click();
        friendButton.click();
        return new FriendsPage();
    }

    public FriendsPage clickOnSignOutButtonUnderPersonIcon() {
        personIcon.click();
        signOutButton.click();
        return new FriendsPage();
    }
}
