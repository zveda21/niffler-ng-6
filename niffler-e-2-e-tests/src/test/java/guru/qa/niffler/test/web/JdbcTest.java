package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.*;
import guru.qa.niffler.service.AuthUserDbClient;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDBClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
       String username = "testbyzi3";
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
        UserDBClient userDBClient = new UserDBClient();
        byte[] emptyData = new byte[0];
        byte[] emptyData2 = new byte[0];

        UserEntity userEntity = userDBClient.create(
                new UserEntity(
                        null,
                        "zvedik2",
                        CurrencyValues.USD,
                        "test",
                        "test",
                        "test",
                        emptyData,
                        emptyData2
                )
        );
        System.out.println(userEntity.getId());
    }
}
