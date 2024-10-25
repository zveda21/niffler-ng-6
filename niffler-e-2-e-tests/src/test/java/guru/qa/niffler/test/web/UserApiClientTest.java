package guru.qa.niffler.test.web;

import guru.qa.niffler.api.UserApiClient;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserApiClientTest {


    private UserApiClient userApiClient;

    @BeforeEach
    public void setUp() {
        userApiClient = new UserApiClient();
    }

    @Test
    public void testCreateUser() {
        String username = "zvedik";
        String password = "12345";

        // Create the user
        UserJson createdUser = userApiClient.createUser(username, password);

        // Verify the user is created successfully
        assertNotNull(createdUser);
        assertNotNull(createdUser.id());
        assertEquals(username, createdUser.username());

    }
}