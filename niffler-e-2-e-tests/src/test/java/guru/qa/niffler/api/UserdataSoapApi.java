package guru.qa.niffler.api;

import guru.qa.jaxb.userdata.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserdataSoapApi {

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> currentUser(@Body CurrentUserRequest currentUserRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> allUsers(@Body AllUsersRequest allUsersRequest);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> friends(@Body FriendsRequest username);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> friendsPage(@Body FriendsPageRequest request);

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-Charset: utf-8"
    })
    @POST("ws")
    Call<Void> removeFriend(@Body RemoveFriendRequest request);

    @Headers(
            value = {
                    "Content-Type: text/xml",
                    "Accept-Charset: utf-8",
            }
    )
    @POST("ws")
    Call<UserResponse> sendInvitation(@Body SendInvitationRequest sendInvitation);

    @Headers(
            value = {
                    "Content-Type: text/xml",
                    "Accept-Charset: utf-8",
            }
    )
    @POST("ws")
    Call<UserResponse> declineInvitation(@Body DeclineInvitationRequest declineInvitationRequest);

    @Headers(
            value = {
                    "Content-Type: text/xml",
                    "Accept-Charset: utf-8",
            }
    )
    @POST("ws")
    Call<UserResponse> acceptInvitation(@Body AcceptInvitationRequest acceptInvitationRequest);
}
