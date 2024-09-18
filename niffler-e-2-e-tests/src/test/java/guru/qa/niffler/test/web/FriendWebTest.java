package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension2;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension2.UserType.Type.*;

@ExtendWith(BrowserExtension.class)
public class FriendWebTest {
    private static final Config CFG = Config.getInstance();

//    @Test
//    void testWithEmptyUser0(@UsersQueueExtension.UserType (empty = true)UsersQueueExtension.StaticUser user) throws InterruptedException {
//        Thread.sleep(1000);
//        System.out.println("user -- " + user);
//    }
//
//    @Test
//    void testWithEmptyUser1(@UsersQueueExtension.UserType (empty = true)UsersQueueExtension.StaticUser user) throws InterruptedException {
//        Thread.sleep(1000);
//        System.out.println("user -- " + user);
//    }
//    @Test
//    void testWithEmptyUser2(@UsersQueueExtension.UserType (empty = false)UsersQueueExtension.StaticUser user) throws InterruptedException {
//        Thread.sleep(1000);
//        System.out.println("user -- " + user);
//    }
//
//    @Test
//    void testWithEmptyUser3(@UsersQueueExtension.UserType (empty = false)UsersQueueExtension.StaticUser user) throws InterruptedException {
//        Thread.sleep(1000);
//        System.out.println("user -- " + user);
//    }

    @Test
    @ExtendWith(UsersQueueExtension2.class)
    void friendShouldBePresentInFriendsTable(@UsersQueueExtension2.UserType(WITH_FRIEND) UsersQueueExtension2.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .checkIfFriendNameIsVisible(user.friend());
    }

    @Test
    @ExtendWith(UsersQueueExtension2.class)
    void friendsTableShouldBeEmptyForNewUser(@UsersQueueExtension2.UserType(EMPTY) UsersQueueExtension2.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .checkIfNoFriendsInPanel("There are no users yet");
    }

    @Test
    @ExtendWith(UsersQueueExtension2.class)
    void incomeInvitationBePresentInFriendsTable(@UsersQueueExtension2.UserType(WITH_INCOME_REQUEST) UsersQueueExtension2.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnFriendButton()
                .checkIfFriendNameIsVisibleInRequestsList(user.income());
    }

    @Test
    @ExtendWith(UsersQueueExtension2.class)
    void outcomeInvitationBePresentInAllPeoplesTable(@UsersQueueExtension2.UserType(WITH_OUTCOME_REQUEST) UsersQueueExtension2.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().clickOnFriendButtonUnderPersonIcon()
                .clickOnAllPeopleButton()
                .checkIfOutcomeFriendRequestIsVisibleInAllList(user.outcome());
    }

}
