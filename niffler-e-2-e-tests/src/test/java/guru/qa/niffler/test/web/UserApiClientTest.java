package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserApiClientTest {


    private UsersApiClient userApiClient;

    @BeforeEach
    public void setUp() {
        userApiClient = new UsersApiClient();
    }

    @Test
    public void testCreateUser() {
        String username = "zvedik04";
        String password = "12345";

        // Create the user
        UserJson createdUser = userApiClient.createUser(username, password);

        // Verify the user is created successfully
        assertNotNull(createdUser);
        assertNotNull(createdUser.id());
        assertEquals(username, createdUser.username());

    }
}