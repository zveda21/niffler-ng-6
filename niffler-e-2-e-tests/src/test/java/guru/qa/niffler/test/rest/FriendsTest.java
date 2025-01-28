package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.FriendshipStatus;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.GatewayApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class FriendsTest {

  @RegisterExtension
  private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

  private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

  @User(friends = 2, incomeInvitations = 1)
  @ApiLogin
  @Test
  void allFriendsAndIncomeInvitationsShouldBeReturnedFroUser(UserJson user, @Token String token) {
    final List<UserJson> expectedFriends = user.testData().friends();
    final List<UserJson> expectedInvitations = user.testData().incomeInvitations();

    final List<UserJson> result = gatewayApiClient.allFriends(
        token,
        null
    );

    Assertions.assertNotNull(result);
    assertEquals(3, result.size());

    final List<UserJson> friendsFromResponse = result.stream().filter(
        u -> u.friendshipStatus() == FriendshipStatus.FRIEND
    ).toList();

    final List<UserJson> invitationsFromResponse = result.stream().filter(
        u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
    ).toList();

    assertEquals(2, friendsFromResponse.size());
    assertEquals(1, invitationsFromResponse.size());

    assertEquals(
        expectedInvitations.getFirst().username(),
        invitationsFromResponse.getFirst().username()
    );

    final UserJson firstUserFromRequest = friendsFromResponse.getFirst();
    final UserJson secondUserFromRequest = friendsFromResponse.getLast();

    assertEquals(
        expectedFriends.getFirst().username(),
        firstUserFromRequest.username()
    );

    assertEquals(
        expectedFriends.getLast().username(),
        secondUserFromRequest.username()
    );
  }

  @User(friends = 2, incomeInvitations = 1)
  @ApiLogin
  @Test
  void shouldReturnAllFriendsAndIncomeInvitationsForUser(UserJson user, @Token String token) {
    final List<UserJson> expectedInvitations = user.testData().incomeInvitations();
    final List<UserJson> allFriends = gatewayApiClient.allFriends(token, null);

    Assertions.assertNotNull(allFriends);
    assertEquals(3, allFriends.size());

    final List<UserJson> friendsFromResponse = allFriends.stream()
            .filter(u -> u.friendshipStatus() == FriendshipStatus.FRIEND)
            .toList();

    final List<UserJson> invitationsFromResponse = allFriends.stream()
            .filter(u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED)
            .toList();

    assertEquals(2, friendsFromResponse.size());
    assertEquals(1, invitationsFromResponse.size());
    assertEquals(
            expectedInvitations.getFirst().username(),
            invitationsFromResponse.getFirst().username()
    );
  }

  @User(friends = 1)
  @ApiLogin
  @Test
  void deleteFriend(UserJson user, @Token String token) {
    final List<UserJson> expectedFriends = user.testData().friends();
    gatewayApiClient.removeFriend(token, expectedFriends.getFirst().username());
    List<UserJson> friendList = gatewayApiClient.allFriends(token, null);
    Assertions.assertTrue(friendList.isEmpty());
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void acceptFriendInvitation(@Token String token) {
    final List<UserJson> allFriends = gatewayApiClient.allFriends(token, null);

    assertEquals(1, allFriends.size());
    final UserJson invitation = allFriends.getFirst();

    gatewayApiClient.acceptInvitation(token, new FriendJson(invitation.username()));

    final List<UserJson> allFriendsAfterInvitation = gatewayApiClient.allFriends(token, null);
    assertEquals(1, allFriendsAfterInvitation.size());
    assertEquals(FriendshipStatus.FRIEND, allFriendsAfterInvitation.getFirst().friendshipStatus());
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void declineFriendInvitation(@Token String token) {
    final List<UserJson> allFriends = gatewayApiClient.allFriends(token, null);

    assertEquals(1, allFriends.size());
    final UserJson invitation = allFriends.getFirst();

    gatewayApiClient.declineInvitation(token, new FriendJson(invitation.username()));

    final List<UserJson> allFriendsAfterInvitation = gatewayApiClient.allFriends(token, null);
    Assertions.assertTrue(allFriendsAfterInvitation.isEmpty());
  }

  @User(outcomeInvitations = 1)
  @ApiLogin
  @Test
  void shouldReceiveOutcomeInvitation(UserJson user, @Token String token) {
    final String expectedUsername = user.testData().outcomeInvitations().getLast().username();
    final String expectedUserPassword = "12345";

    final AuthApiClient authApiClient = new AuthApiClient();
    ThreadSafeCookieStore.INSTANCE.removeAll();
    String expectedUserToken = "Bearer " + authApiClient.login(expectedUsername, expectedUserPassword);

    List<UserJson> friendResult = gatewayApiClient.allFriends(expectedUserToken, null);
    final List<UserJson> incomeInvitationFromResponse = friendResult.stream().filter(
            u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
    ).toList();

    Assertions.assertEquals(1, incomeInvitationFromResponse.size());
    Assertions.assertEquals(user.username(), incomeInvitationFromResponse.getFirst().username());
  }

  @User(outcomeInvitations = 1)
  @ApiLogin
  @Test
  void receiveInvitation(UserJson user, @Token String token) {
    final String expectedInvitedUsername = user.testData().outcomeInvitations().getLast().username();
    final String expectedUserPassword = "12345";

    final AuthApiClient authApiClient = new AuthApiClient();
    ThreadSafeCookieStore.INSTANCE.removeAll();
    String expectedUserToken = "Bearer " + authApiClient.login(expectedInvitedUsername, expectedUserPassword);

    List<UserJson> allFriends = gatewayApiClient.allFriends(expectedUserToken, null);
    final List<UserJson> receivedInvitations = allFriends.stream().filter(
            u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
    ).toList();

    assertEquals(1, receivedInvitations.size());
    assertEquals(user.username(), receivedInvitations.getFirst().username());
  }
}
