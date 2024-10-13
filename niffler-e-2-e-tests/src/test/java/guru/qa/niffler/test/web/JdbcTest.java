package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.*;
import guru.qa.niffler.service.AuthUserDbClient;
import guru.qa.niffler.service.CategoryDbClient;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

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
    void findSpendingByIdTest() {
        SpendDbClient client = new SpendDbClient();
        SpendJson spend = client.findSpendById(UUID.fromString("77ba42f4-7e54-11ef-9898-0242ac110002"));
        System.out.println("spend--" + spend);
    }

    @Test
    void findSpendingByUsernameTest() {
        SpendDbClient client = new SpendDbClient();
        List<SpendJson> spendJsonList = client.findAllSpendByUsername("duck");
        for (SpendJson spendJson : spendJsonList) {
            System.out.println(spendJson);
        }
    }

    @Test
    void checkIfSpendingDeletedBId() {
        SpendDbClient client = new SpendDbClient();
        SpendJson spendToDelete = client.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "Test by zi 2",
                                "duck",
                                false
                        ),
                        CurrencyValues.USD,
                        47.0,
                        "test",
                        "duck"
                )
        );
        System.out.println(spendToDelete);
        client.deleteSpending(spendToDelete);
        SpendJson deletedSpend = client.findSpendById(spendToDelete.id());
        assertNull(deletedSpend, "Spend should be deleted and not found");

    }

    @Test
    void userDaoTest() {
        UserDbClient userDBClient = new UserDbClient();
        byte[] emptyData = new byte[0];
        byte[] emptyData2 = new byte[0];

        UserEntity userEntity = userDBClient.create(
                new UserJson(
                        null,
                        CurrencyValues.USD,
                        "zvedik2",
                        "test",
                        null,
                        null,
                        "test",
                        "test",
                        null
                )
        );
        System.out.println(userEntity.getId());
    }

    @Test
    void springJdbcTest() {
        UserDbClient usersDbClient = new UserDbClient();
        UserJson user = usersDbClient.createUser(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        "valentin-13",
                        "valentin-13",
                        "valentin-13",
                        "valentin-14",
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createSpendUsingSpringJdbcTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spend = spendDbClient.createSpendUsingSpringJdbc(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-47_byzi",
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
    void findAllSpendSpringJdbcTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        for (int i = 0; i < spendDbClient.findAllSpendsSpringJdbc().size(); i++) {
            System.out.println("spends--" + spendDbClient.findAllSpendsSpringJdbc().get(i));
        }
    }

    @Test
    void findAllSpendsTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        for (int i = 0; i < spendDbClient.findAllSpendsSpringJdbc().size(); i++) {
            System.out.println("spends--" + spendDbClient.findAllSpendsSpringJdbc().get(i));
        }
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

    @Test
    void createUserSpringJdbcTransactionTest() {
        UserDbClient usersDbClient = new UserDbClient();
        UserJson user = usersDbClient.createUserSpringJdbcTransaction(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        "valentin-13",
                        "valentin-13",
                        "valentin-13",
                        "TestByZi44",
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserSpringJdbcWithoutTransactionTest() {
        UserDbClient usersDbClient = new UserDbClient();
        UserJson user = usersDbClient.createUserWithoutSpringJdbcTransaction(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        "valentin-13",
                        "valentin-13",
                        "valentin-13",
                        "TestByZi46",
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserJdbcTransactionTest() {
        UserDbClient usersDbClient = new UserDbClient();
        UserJson user = usersDbClient.createUserJdbcTransaction(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        "valentin-13",
                        "valentin-13",
                        "valentin-13",
                        "TestByZi47",
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserJdbcWithoutTransactionTest() {
        UserDbClient usersDbClient = new UserDbClient();
        UserJson user = usersDbClient.createUserWithoutJdbcTransaction(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        "valentin-17",
                        "valentin-17",
                        "valentin-17",
                        "TestByZi50",
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserJdbcWithRepo() {
        UserDbClient usersDbClient = new UserDbClient();
        UserJson user = usersDbClient.createUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "valentin-71",
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserJdbcWithRepo1() {
        UserDbClient usersDbClient = new UserDbClient();
        UserJson requester = usersDbClient.createUDUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "requester23",
                        null
                )
        );
        System.out.println("requester-" + requester);
        UserJson addressee = usersDbClient.createUDUserWithRepo(
                new UserJson(
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "addressee23",
                        null
                )
        );
        System.out.println("addressee-" + addressee);

        usersDbClient.addIncomeInvitation(requester, addressee);
        usersDbClient.addOutcomeInvitation(requester,addressee);
        usersDbClient.addFriend(requester, addressee);

    }

    static UserDbClient usersDbClient = new UserDbClient();

    @ValueSource(strings = {
            "valentin-25",
    })
    @ParameterizedTest
    void springJdbcTest(String uname) {

        UserJson user = usersDbClient.createUser(
                uname,
                "12345"
        );

        usersDbClient.addIncomeInvitation(user, 1);
        usersDbClient.addOutcomeInvitation(user, 1);
    }
}
