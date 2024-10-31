package guru.qa.niffler.service.impl;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.apache.kafka.common.errors.TimeoutException;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";


    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);
    private final UserApi userApi = new RestClient.EmptyClient(CFG.userdataUrl()).create(UserApi.class);

    @Override
    public @Nonnull UserJson createUser(@Nonnull String username, String password){
        try {
            authApi.requestRegisterForm().execute();

            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();

            Stopwatch sw = Stopwatch.createStarted();
            long maxWaitTime = 5000L;
            while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
                UserJson userJson = userApi.getCurrentUser(username).execute().body();
                if (userJson != null && userJson.id() != null) {
                    return userJson;
                } else {
                    Thread.sleep(100);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        throw new TimeoutException("User registration timed out for username: " + username);
    }

    @Step("Adding {count} incoming invitations to user: {targetUser.username}")
    @Override
    public void createIncomeInvitations(UserJson targetUser, int count){
        if (count > 0) {
            UserJson user = getCurrentUser(targetUser.username());
            if (user == null || user.id() == null) {
                throw new AssertionError("User with name " + targetUser.username() + " not found");
            }
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                UserJson newUser;

                try {
                    newUser = createUser(username, defaultPassword);
                    Response<UserJson> response = userApi.sendInvitation(
                            newUser.username(),
                            user.username()
                    ).execute();

                    if (response.code() != 200) {
                        throw new AssertionError("Failed to send invitation: " + response.message());
                    }
                    System.out.println("Sent invitation from " + newUser.username() + " to " + user.username());
                    targetUser.testData().incomeInvitations().add(newUser);
                } catch (IOException e) {
                    throw new AssertionError("IOException occurred while adding income invitation: " + e.getMessage());
                }
            }
        }
    }


    @Step("Adding {count} outcome invitations to user: {targetUser.username}")
    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserJson user = getCurrentUser(targetUser.username());
            if (user == null || user.id() == null) {
                throw new AssertionError("User with name " + targetUser.username() + " not found");
            }
            for (int i = 0; i < count; i++) {
                UserJson newUser = createUser(randomUsername(), "12345");
                sendInvitation(user.username(), newUser.username());
                System.out.println("Sent invitation from " + user.username() + " to " + newUser.username());
            }
        }
    }

    @Step("Adding {count} friends to user: {targetUser.username}")
    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            UserJson user = getCurrentUser(targetUser.username());
            if (user == null || user.id() == null) {
                throw new AssertionError("User with name " + targetUser.username() + " not found");
            }
            for (int i = 0; i < count; i++) {
                UserJson newUser = createUser(randomUsername(), "12345");
                acceptInvitation(newUser.username(), user.username());
                System.out.println("Sent invitation from " + newUser.username() + " to " + user.username());
            }
        }
    }

    public UserJson getCurrentUser(String username) {
        final Response<UserJson> response;
        try {
            response = userApi.getCurrentUser(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public void sendInvitation(String addresseeUsername, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = userApi.sendInvitation(addresseeUsername, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    public void acceptInvitation(String receiverUsername, String username) {
        final Response<Void> response;
        try {
            response = userApi.acceptInvitation(receiverUsername, username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

}