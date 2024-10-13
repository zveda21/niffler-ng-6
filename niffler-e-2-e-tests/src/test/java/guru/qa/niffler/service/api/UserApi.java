package guru.qa.niffler.service.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {

    @POST("/users")
    Call<UserJson> createUser(@Body UserJson user);

    @POST("/invitations/income")
    Call<Void> createIncomeInvitations(@Body UserJson targetUser, @Query("count") int count);

    @POST("/invitations/outcome")
    Call<Void> createOutcomeInvitations(@Body UserJson targetUser, @Query("count") int count);

    @POST("/friends")
    Call<Void> createFriends(@Body UserJson targetUser, @Query("count") int count);
}
