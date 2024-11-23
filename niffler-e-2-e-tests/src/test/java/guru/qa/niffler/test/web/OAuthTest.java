package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class OAuthTest {
    private final AuthApiClient authApiClient = new AuthApiClient();

    @User
    @Test
    void oauthTest(UserJson user) throws IOException {
        String token = authApiClient.login(user.username(), user.testData().password());
        System.out.println(token);
        Assertions.assertNotNull(token);
    }
}
