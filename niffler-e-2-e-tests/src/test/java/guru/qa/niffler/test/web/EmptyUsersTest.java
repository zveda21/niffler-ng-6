package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmptyUsersTest {

    @Test
    @User
    void checkIfUsersListIsEmpty(UserJson user){
        UsersClient usersClient = new UsersApiClient();
        List<UserJson> response = usersClient.getAllUsers(user.username(),null);
        assertTrue(response.isEmpty(),"The users list is empty");
    }
}
