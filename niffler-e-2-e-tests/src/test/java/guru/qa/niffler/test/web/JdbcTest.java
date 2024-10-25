package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.AuthUserDbClient;
import guru.qa.niffler.service.CategoryDbClient;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

public class JdbcTest {

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-46_byzi",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx_byzi",
                        "duck"
                )
        );

        System.out.println("spend--" + spend);
    }

    @Test
    void createAuthUserTest() {
        AuthUserDbClient authUserDbClient = new AuthUserDbClient();
        String username = "testbyzi8";
        AuthUserJson authUserJson = authUserDbClient.createAuthUser(
                new AuthUserJson(
                        null,
                        "username",
                        true,
                        true,
                        true,
                        true,
                        "12345"
                )
        );
        System.out.println("userId--- " + authUserJson);

    }

    @Test
    void daoTest() {
        SpendDbClient client = new SpendDbClient();
        SpendJson spend = client.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "Test by zi 3",
                                "duck",
                                false
                        ),
                        CurrencyValues.USD,
                        47.0,
                        "test",
                        "duck"
                )
        );
        System.out.println(spend);
    }

    @Test
    void findAllCategoriesSpringJdbcTest() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();
        for (int i = 0; i < categoryDbClient.findAllCategories().size(); i++) {
            System.out.println("categories--" + categoryDbClient.findAllCategories().get(i));
        }
    }

    @Test
    void findAllCategoriesTest() {
        CategoryDbClient categoryDbClient = new CategoryDbClient();
        for (int i = 0; i < categoryDbClient.findAllCategories().size(); i++) {
            System.out.println("categories--" + categoryDbClient.findAllCategories().get(i));
        }
    }


    static UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(strings = {
            "zveda15"
    })
    @ParameterizedTest
    void springJdbcTestJNDI(String uname) {

        UserJson user = usersDbClient.createUser(
                uname,
                "12345"
        );

        usersDbClient.createIncomeInvitations(user, 1);
        usersDbClient.createOutcomeInvitations(user, 1);
    }
}
