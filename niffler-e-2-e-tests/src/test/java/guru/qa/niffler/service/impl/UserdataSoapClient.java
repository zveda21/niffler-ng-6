package guru.qa.niffler.service.impl;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }

    @NotNull
    @Step("Get current user info using SOAP")
    public UserResponse currentUser(CurrentUserRequest request) throws IOException {
        return Objects.requireNonNull(userdataSoapApi.currentUser(request).execute().body());
    }

    @NotNull
    @Step("Get all users info using SOAP")
    public UsersResponse allUsers(AllUsersRequest request) throws IOException {
        return Objects.requireNonNull(userdataSoapApi.allUsers(request).execute().body());
    }

    @NotNull
    @Step("Get all users friends info using SOAP")
    public UsersResponse friends(FriendsRequest friendsRequest) throws IOException {
        return Objects.requireNonNull(userdataSoapApi.friends(friendsRequest).execute().body());
    }

    @NotNull
    @Step("Get all users friendsPage info using SOAP")
    public UsersResponse friendsPage(FriendsPageRequest friendsPageRequest) throws IOException {
        return Objects.requireNonNull(userdataSoapApi.friendsPage(friendsPageRequest).execute().body());
    }

    @Step("Remove Friend using SOAP")
    public Void removeFriend(RemoveFriendRequest removeFriendRequest) throws IOException {
        try {
            return Objects.requireNonNull(userdataSoapApi.removeFriend(removeFriendRequest).execute().body());
        } catch (NullPointerException e) {
            throw new RuntimeException("Failed to remove friend. Response body is null", e);
        }
    }

    @NotNull
    @Step("Send friend invitation using SOAP")
    public UserResponse sendInvitation(SendInvitationRequest sendInvitationRequest) throws IOException {
        return Objects.requireNonNull(userdataSoapApi.sendInvitation(sendInvitationRequest).execute().body());
    }

    @NotNull
    @Step("Decline friend invitation using SOAP")
    public UserResponse declineInvitation(DeclineInvitationRequest declineInvitationRequest) throws IOException {
        return Objects.requireNonNull(userdataSoapApi.declineInvitation(declineInvitationRequest).execute().body());
    }

    @NotNull
    @Step("Accept friend invitation using SOAP")
    public UserResponse acceptInvitation(AcceptInvitationRequest acceptInvitationRequest) throws IOException {
        return Objects.requireNonNull(userdataSoapApi.acceptInvitation(acceptInvitationRequest).execute().body());
    }
}
