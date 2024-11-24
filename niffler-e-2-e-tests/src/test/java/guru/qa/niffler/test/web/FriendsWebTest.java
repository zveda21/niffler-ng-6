package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsWebTest {

  private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

  @User(friends = 1)
  @Test
  @ApiLogin
  void friendShouldBePresentInFriendsTable(UserJson user) {
    final String friendUsername = user.testData().friendsUsernames()[0];

    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .checkExistingFriends(friendUsername);
  }

  @User
  @Test
  @ApiLogin
  void friendsTableShouldBeEmptyForNewUser(UserJson user) {
    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .checkExistingFriendsCount(0);
  }

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    final String incomeInvitationUsername = user.testData().incomeInvitationsUsernames()[0];

    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .checkExistingInvitations(incomeInvitationUsername);
  }

  @User(outcomeInvitations = 1)
  @Test
  @ApiLogin
  void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
    final String outcomeInvitationUsername = user.testData().outcomeInvitationsUsernames()[0];

    Selenide.open(PeoplePage.URL, PeoplePage.class)
        .checkInvitationSentToUser(outcomeInvitationUsername);
  }

  @User(friends = 1)
  @Test
  @ApiLogin
  void shouldRemoveFriend(UserJson user) {
    final String userToRemove = user.testData().friendsUsernames()[0];

    Selenide.open(FriendsPage.URL, FriendsPage.class)
        .removeFriend(userToRemove)
        .checkExistingFriendsCount(0);
  }

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  void shouldAcceptInvitation(UserJson user) {
    final String userToAccept = user.testData().incomeInvitationsUsernames()[0];

    FriendsPage friendsPage = open(FriendsPage.URL, FriendsPage.class)
        .checkExistingInvitationsCount(1)
        .acceptFriendInvitationFromUser(userToAccept);

    driver.refresh();

    new FriendsPage(driver).checkExistingInvitationsCount(0)
        .checkExistingFriendsCount(1)
        .checkExistingFriends(userToAccept);
  }

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  void shouldDeclineInvitation(UserJson user) {
    final String userToDecline = user.testData().incomeInvitationsUsernames()[0];

    FriendsPage friendsPage = Selenide.open(FriendsPage.URL, FriendsPage.class)
        .checkExistingInvitationsCount(1)
        .declineFriendInvitationFromUser(userToDecline);

    driver.refresh();

    new FriendsPage(driver).checkExistingInvitationsCount(0)
        .checkExistingFriendsCount(0);

    driver.open(PeoplePage.URL);
    new PeoplePage(driver).checkExistingUser(userToDecline);
  }
}
