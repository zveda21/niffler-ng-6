package guru.qa.niffler.test.soap;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UserdataSoapClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SoapTest
public class SoapUsersTest {

    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

    @Test
    @User
    void currentUserTest(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());
        UserResponse response = userdataSoapClient.currentUser(request);
        assertEquals(
                user.username(),
                response.getUser().getUsername()
        );
    }

    @Test
    @User
    void checkCurrentUserData(UserJson user) throws Exception {
        CurrentUserRequest cur = new CurrentUserRequest();
        cur.setUsername(user.username());
        final UserResponse currentUserResponse = userdataSoapClient.currentUser(cur);
        assertEquals(user.username(), currentUserResponse.getUser().getUsername());
        assertEquals(Currency.RUB, currentUserResponse.getUser().getCurrency());
        assertEquals(FriendshipStatus.VOID, currentUserResponse.getUser().getFriendshipStatus());
    }

    @User(friends = 10)
    @Test
    public void getFriendsListWithPaginationAndFilter(UserJson user) throws IOException {
        FriendsPageRequest friendsPageRequest = new FriendsPageRequest();
        friendsPageRequest.setUsername(user.username());
        PageInfo info = new PageInfo();
        info.setPage(0);
        info.setSize(10);
        friendsPageRequest.setPageInfo(info);
        UsersResponse userResponse = userdataSoapClient.friendsPage(friendsPageRequest);
        assertEquals(10, userResponse.getUser().size());

    }

    @User(friends = 10)
    @Test
    public void getFriendsListWithQuery(UserJson user) throws IOException {
        String searchQuery = user.testData().friends().getFirst().username();
        FriendsPageRequest friendsPageRequest = new FriendsPageRequest();
        friendsPageRequest.setUsername(user.username());
        PageInfo info = new PageInfo();
        info.setPage(0);
        info.setSize(10);
        friendsPageRequest.setPageInfo(info);
        friendsPageRequest.setSearchQuery(searchQuery);
        UsersResponse userResponse = userdataSoapClient.friendsPage(friendsPageRequest);
        assertNotNull(userResponse);
        assertEquals(1, userResponse.getUser().size());
        assertEquals(searchQuery, userResponse.getUser().getFirst().getUsername());
    }

    @User(friends = 1)
    @Test
    public void deleteFriendshipTest(UserJson user) throws IOException {
        String removedFriend = user.testData().friends().getFirst().username();
        RemoveFriendRequest removeFriendRequest = new RemoveFriendRequest();
        removeFriendRequest.setUsername(user.username());
        removeFriendRequest.setFriendToBeRemoved(removedFriend);
        userdataSoapClient.removeFriend(removeFriendRequest);

        CurrentUserRequest currentUserRequest = new CurrentUserRequest();
        currentUserRequest.setUsername(user.username());
        UserResponse userResponse = userdataSoapClient.currentUser(currentUserRequest);
        assertEquals(FriendshipStatus.VOID, userResponse.getUser().getFriendshipStatus());
    }


    @User(incomeInvitations = 1)
    @Test
    public void sendFriendInvitationTest(UserJson user) throws IOException {
        String friendTo = user.testData().incomeInvitations().getFirst().username();
        SendInvitationRequest sendInvitationRequest = new SendInvitationRequest();
        sendInvitationRequest.setUsername(user.username());
        sendInvitationRequest.setFriendToBeRequested(friendTo);
        UserResponse userResponse = userdataSoapClient.sendInvitation(sendInvitationRequest);
        assertEquals(FriendshipStatus.INVITE_SENT, userResponse.getUser().getFriendshipStatus());
    }

    @User(incomeInvitations = 1)
    @Test
    public void declineFriendInvitationTest(UserJson user) throws IOException {
        String friendTo = user.testData().incomeInvitations().getFirst().username();
        DeclineInvitationRequest declineInvitationRequest = new DeclineInvitationRequest();
        declineInvitationRequest.setUsername(user.username());
        declineInvitationRequest.setInvitationToBeDeclined(friendTo);
        UserResponse userResponse = userdataSoapClient.declineInvitation(declineInvitationRequest);
        assertEquals(FriendshipStatus.VOID, userResponse.getUser().getFriendshipStatus());
    }

    @User(incomeInvitations = 1)
    @Test
    public void acceptFriendInvitationTest(UserJson user) throws IOException {
        String friendTo = user.testData().incomeInvitations().getFirst().username();
        AcceptInvitationRequest acceptInvitationRequest = new AcceptInvitationRequest();
        acceptInvitationRequest.setUsername(user.username());
        acceptInvitationRequest.setFriendToBeAdded(friendTo);
        UserResponse userResponse = userdataSoapClient.acceptInvitation(acceptInvitationRequest);
        assertEquals(FriendshipStatus.FRIEND, userResponse.getUser().getFriendshipStatus());
    }
}
