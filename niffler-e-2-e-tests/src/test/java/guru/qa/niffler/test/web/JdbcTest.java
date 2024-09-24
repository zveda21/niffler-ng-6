package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDBClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

public class JdbcTest {

    @Test
    void daoTest() {
        SpendDbClient client = new SpendDbClient();
        SpendJson spend = client.createSpend(
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
        System.out.println(spend);
    }

    @Test
    void findSpendingByIdTest() {
        SpendDbClient client = new SpendDbClient();
        SpendJson spend = client.findSpendById(UUID.fromString("0e6474c2-ca21-457a-9a0a-a6b792bc2b41"));
        System.out.println(spend);
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
