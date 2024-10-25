package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

    public static String friendsPageUrl = Config.getInstance().frontUrl() + "people/friends";

    private final SelenideElement friendsButton = $("div>a[href='/people/friends']");
    private final SelenideElement allPeopleButton = $("div>a[href='/people/all']");
    private final SelenideElement emptyFriendPanelText = $("#simple-tabpanel-friends [class^='MuiTypography-root']");
    private final ElementsCollection friendsList = $$("#friends>tr p:nth-child(1)");
    private final ElementsCollection friendNameInRequestsList = $$("#requests tr td:nth-child(1) p:nth-child(1)");
    private final ElementsCollection allPeopleList = $$("#all tr");
    private final SelenideElement searchInput = $("[class^='MuiToolbar-root'] input");
    private final ElementsCollection allPeopleNameList = $$("#all>tr p:nth-child(1)");
    private final ElementsCollection allAddFriendButtonList = $$("#all>tr button");
    private final SelenideElement acceptFriendButton = $("[class^='MuiTableCell-root'] button[class*='MuiButton-containedPrimary']");
    private final SelenideElement declineFriendButton = $("[class^='MuiTableCell-root'] button[class*='MuiButton-containedSecondary']");
    private final SelenideElement confirmDeclineFriendButton = $("[class^='MuiDialogActions'] button[class*='MuiButton-containedPrimary']");
    private final SelenideElement unfriendButton = $("button[class*='MuiButton-containedSecondary']");
    private final SelenideElement addFriendButton = $("button[class*='MuiButton-containedSecondary']");


    public FriendsPage clickOnFriendButton() {
        friendsButton.click();
        return this;
    }

    public FriendsPage clickOnAllPeopleButton() {
        allPeopleButton.click();
        return this;
    }

    public void checkIfNoFriendsInPanel(String message) {
        emptyFriendPanelText.shouldHave(text(message));
    }

    public void checkIfFriendNameIsVisible(String friendName) {
        friendsList.findBy(text(friendName)).shouldBe(visible);
    }

    public void checkIfFriendNameIsVisibleInRequestsList(String friendName) {
        friendNameInRequestsList.findBy(text(friendName)).shouldBe(visible);
    }

    public void checkIfOutcomeFriendRequestIsVisibleInAllList(String friendName) {
        allPeopleList.findBy(text(friendName)).shouldBe(visible).shouldHave(text("Waiting..."));
    }

    public void searchUserByName(String username) {
        searchInput.clear();
        searchInput.sendKeys(username);
        searchInput.sendKeys(Keys.RETURN);
    }

    public void isUserVisible(String username) {
        allPeopleNameList.findBy(text(username)).shouldBe(visible);
    }

    public void sendFriendRequest(String username) {
//todo
    }

    @Step("Accept friend request")
    public FriendsPage clickOnAcceptFriendButton() {
        acceptFriendButton.click();
        return this;
    }

    @Step("Decline friend request")
    public FriendsPage clickOnDeclineFriendButton() {
        declineFriendButton.click();
        confirmDeclineFriendButton.click();
        return this;
    }

    @Step("Check if unfriend button is visible")
    public FriendsPage checkIfUnfriendButtonIsVisible() {
        unfriendButton.shouldBe(visible);
        return this;
    }

    @Step("Click on Add friend button")
    public FriendsPage clickOnAddFriendButton() {
        addFriendButton.click();
        return this;
    }
}
