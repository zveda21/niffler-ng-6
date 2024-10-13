package guru.qa.niffler.service.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class UserClientImpl implements UserApi {

    private final UserApi userApi;

    public UserClientImpl(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        this.userApi = retrofit.create(UserApi.class);
    }

    @Override
    public Call<UserJson> createUser(UserJson user) {
        return userApi.createUser(user);
    }

    @Override
    public Call<Void> createIncomeInvitations(UserJson targetUser, int count) {
        return userApi.createIncomeInvitations(targetUser, count);
    }

    @Override
    public Call<Void> createOutcomeInvitations(UserJson targetUser, int count) {
        return userApi.createOutcomeInvitations(targetUser, count);
    }

    @Override
    public Call<Void> createFriends(UserJson targetUser, int count) {
        return userApi.createFriends(targetUser, count);
    }
}
