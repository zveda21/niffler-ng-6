package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserApiClient {

    private final Retrofit userRetrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final Retrofit authRetrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserApi userApi = userRetrofit.create(UserApi.class);
    private final AuthApi authApi = authRetrofit.create(AuthApi.class);

    // Create user method
    public UserJson createUser(String username, String password) {
        // Step 1: Request the registration form to retrieve the CSRF token
        final Response<Void> formResponse;
        try {
            formResponse = authApi.requestRegisterForm().execute();
        } catch (IOException e) {
            throw new AssertionError("Error while requesting registration form", e);
        }

        // Assert that the form request was successful
        assertEquals(200, formResponse.code(), "Expected code 200 when requesting registration form");

        // Step 2: Extract the CSRF token from the response headers
        String cookieHeader = formResponse.headers().get("Set-Cookie");
        if (cookieHeader == null || !cookieHeader.contains("XSRF-TOKEN")) {
            throw new AssertionError("Failed to retrieve XSRF-TOKEN from Set-Cookie header");
        }

        String csrfToken = null;
        for (String cookie : cookieHeader.split(";")) {
            if (cookie.contains("XSRF-TOKEN")) {
                csrfToken = cookie.split("=")[1].trim();
                break;
            }
        }

        if (csrfToken == null) {
            throw new AssertionError("XSRF-TOKEN not found in Set-Cookie header");
        }

        // Step 3: Send the registration request
        final Response<Void> registerResponse;
        try {
            registerResponse = authApi.register(username, password, password, csrfToken).execute();
        } catch (IOException e) {
            throw new AssertionError("Error while requesting user registration", e);
        }

        // Assert that the registration was successful
        assertEquals(201, registerResponse.code(), "Expected code 201 for successful registration");

        // Step 4: Retrieve the current user information
        final Response<UserJson> userResponse;
        try {
            userResponse = userApi.getCurrentUser(username).execute();
        } catch (IOException e) {
            throw new AssertionError("Error while retrieving user data", e);
        }

        assertEquals(200, userResponse.code(), "Expected code 200 while retrieving user data");

        UserJson user = userResponse.body();
        if (user == null) {
            throw new AssertionError("User was not successfully created, response is null");
        }
        if (user.id() == null) {
            throw new AssertionError("User was not successfully created, ID is missing");
        }
        return user;
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

    public void sendInvitation(String addresseeUsername) {
        final Response<Void> response;
        try {
            response = userApi.sendInvitation(addresseeUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    public void acceptInvitation(String receiverUsername) {
        final Response<Void> response;
        try {
            response = userApi.acceptInvitation(receiverUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    public void deleteUser(int id) {
        throw new UnsupportedOperationException("Удаление пользователей не поддерживается.");
    }
}
