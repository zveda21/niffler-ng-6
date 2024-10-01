package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                                .create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            new SpendDaoJdbc(connection).create(spendEntity)
                    );
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL
        );
    }

    public SpendJson createSpendUsingSpringJdbc(SpendJson spendJson) {
        SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl()))
                    .create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        SpendEntity createdSpendEntity = new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl()))
                .create(spendEntity);

        return SpendJson.fromEntity(createdSpendEntity);
    }

    public List<SpendJson> findAllSpendsSpringJdbc() {
        List<SpendEntity> spendEntities = new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAll();
        List<SpendJson> spendJsonList = new ArrayList<>();
        for (SpendEntity spendEntity : spendEntities) {
            spendJsonList.add(SpendJson.fromEntity(spendEntity));
        }
        return spendJsonList;
    }

    public List<SpendJson> findAllSpends() {
        return transaction(connection -> {
            List<SpendEntity> spendEntities = new SpendDaoJdbc(connection).findAll();
            List<SpendJson> spendJsonList = new ArrayList<>();
            for (SpendEntity spendEntity : spendEntities) {
                spendJsonList.add(SpendJson.fromEntity(spendEntity));
            }
            return spendJsonList;
        }, CFG.spendJdbcUrl(), TRANSACTION_ISOLATION_LEVEL);
    }


    public SpendJson findSpendById(UUID spendId) {
        return transaction(connection -> {
                    Optional<SpendEntity> optionalSpend = new SpendDaoJdbc(connection).findSpendById(spendId);
                    return optionalSpend.map(SpendJson::fromEntity).orElse(null);
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }

    public List<SpendJson> findAllSpendByUsername(String username) {
        return transaction(connection -> {
                    List<SpendEntity> spendEntities = new SpendDaoJdbc(connection).findAllByUsername(username);
                    List<SpendJson> spendJsonList = new ArrayList<>();
                    for (SpendEntity spendEntity : spendEntities) {
                        spendJsonList.add(SpendJson.fromEntity(spendEntity));
                    }
                    return spendJsonList;
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }

    public void deleteSpending(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("SpendJson cannot be null");
        }
        transaction(connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    new SpendDaoJdbc(connection).deleteSpend(spendEntity);
                    return null;
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }
}
