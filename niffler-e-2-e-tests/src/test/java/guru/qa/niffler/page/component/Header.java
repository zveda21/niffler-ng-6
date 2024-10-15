package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class Header {
    private final SelenideElement self = $("#root header ");

    public void checkHeaderTitle() {
        self.$("h1").shouldHave(text("Niffler"));
    }

    private void navigateTo(String url) {
        open(url);
    }

    public FriendsPage toFriendsPage() {
        navigateTo(FriendsPage.friendsPageUrl);
        return new FriendsPage();
    }

    public PeoplePage toAllPeoplesPage() {
        navigateTo(PeoplePage.allPeoplePageUrl);
        return new PeoplePage();
    }

    public ProfilePage toProfilePage() {
        navigateTo(ProfilePage.profilePageUrl);
        return new ProfilePage();
    }

    public EditSpendingPage addSpendingPage() {
        navigateTo(EditSpendingPage.spendingPageUrl);
        return new EditSpendingPage();
    }

    public MainPage toMainPage() {
        navigateTo(MainPage.mainPageUrl);
        return new MainPage();
    }
}
