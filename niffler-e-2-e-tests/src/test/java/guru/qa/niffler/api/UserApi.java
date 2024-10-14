package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApi {

    @GET("/internal/users/current")
    Call<UserJson> getCurrentUser(@Query("username") String username);

    // sendInvitation
    @POST("/internal/invitations/send")
    Call<Void> sendInvitation(@Body String addresseeUsername);

    // acceptInvitation
    @POST("/internal/invitations/outcome")
    Call<Void> acceptInvitation(@Body String receiverUsername);

    default void deleteUser(int id) {
        throw new UnsupportedOperationException("Удаление пользователей не поддерживается.");
    }
}