package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class Header {
    private final SelenideElement self = $("#root header ");

    @Step("Check header title")
    public void checkHeaderTitle() {
        self.$("h1").shouldHave(text("Niffler"));
    }

    private void navigateTo(String url) {
        open(url);
    }

    @Step("Navigate to FriendPage")
    public FriendsPage toFriendsPage() {
        navigateTo(FriendsPage.friendsPageUrl);
        return new FriendsPage();
    }

    @Step("Navigate to PeoplePage")
    public PeoplePage toAllPeoplePage() {
        navigateTo(PeoplePage.allPeoplePageUrl);
        return new PeoplePage();
    }

    @Step("Navigate to ProfilePage")
    public ProfilePage toProfilePage() {
        navigateTo(ProfilePage.profilePageUrl);
        return new ProfilePage();
    }

    @Step("Navigate to EditSpendingPage")
    public EditSpendingPage addSpendingPage() {
        navigateTo(EditSpendingPage.spendingPageUrl);
        return new EditSpendingPage();
    }

    @Step("Navigate to MainPage")
    public MainPage toMainPage() {
        navigateTo(MainPage.url);
        return new MainPage();
    }
}
