package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.service.impl.UserDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendWebTest {
    private static final Config CFG = Config.getInstance();
    private final UserDbClient usersDbClient = new UserDbClient();
    private final String username = randomUsername();
    private final String username2 = randomUsername();
    private final String password = "12345";


    @Test
    void friendShouldBePresentInFriendsTable(@UsersQueueExtension.UserType(WITH_FRIEND) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .checkIfFriendNameIsVisible(user.friend());
    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UsersQueueExtension.UserType(EMPTY) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .checkIfNoFriendsInPanel("There are no users yet");
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UsersQueueExtension.UserType(WITH_INCOME_REQUEST) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .checkIfFriendNameIsVisibleInRequestsList(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UsersQueueExtension.UserType(WITH_OUTCOME_REQUEST) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnAllPeopleButton()
                .checkIfOutcomeFriendRequestIsVisibleInAllList(user.outcome());
    }

    @Test
    void searchUserByNameAndSendFriendRequest(@UsersQueueExtension.UserType(WITH_FRIEND) UsersQueueExtension.StaticUser user) {
        String searchUser = "testbyzi48";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnAllPeopleButton();
        new  SearchField().search(searchUser);
        new FriendsPage().clickOnAddFriendButton()
                .checkIfOutcomeFriendRequestIsVisibleInAllList(searchUser);
    }


    @Test
    void checkIfFriendIsInWaitingList(){
        UserJson newUser1 = usersDbClient.createUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username,
                        null,
                        null
                )
        );
        UserJson newUser2 = usersDbClient.createUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username2,
                        null,
                        null
                )
        );
        usersDbClient.addIncomeInvitation(newUser1, newUser2);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(newUser2.username(), password);

        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .clickOnAcceptFriendButton()
                .checkIfUnfriendButtonIsVisible();
        new  SearchField().search(username2);
        new FriendsPage().checkIfOutcomeFriendRequestIsVisibleInAllList(username2);
    }

    @Test
    void checkAddFriendFunctionality() throws InterruptedException {
        UserJson newUser1 = usersDbClient.createUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username,
                        null,null
                )
        );
        UserJson newUser2 = usersDbClient.createUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username2,
                        null,
                        null
                )
        );
        usersDbClient.addIncomeInvitation(newUser1, newUser2);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(username2, password);

        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .clickOnAcceptFriendButton()
                .checkIfUnfriendButtonIsVisible();
    }

    @Test
    void checkDeclineFriendFunctionality() {
        UserJson newUser1 = usersDbClient.createUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username,
                        null,
                        null
                )
        );
        UserJson newUser2 = usersDbClient.createUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username2,
                        null,
                        null
                )
        );
        usersDbClient.addIncomeInvitation(newUser1, newUser2);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(username2, password);

        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .clickOnDeclineFriendButton();
    }
}
