package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.SpendJson;
import lombok.NonNull;

import java.sql.Connection;
import java.util.*;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
    private final SpendDao spendDao = new SpendDaoSpringJdbc();
    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());


    public @NonNull SpendJson createSpend(@NonNull SpendJson spend) {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendDao.create(spendEntity));
                },
                TRANSACTION_ISOLATION_LEVEL
        ));
    }

    public @NonNull SpendJson createSpendUsingSpringJdbc(@NonNull SpendJson spendJson) {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    SpendEntity createdSpendEntity = spendDao.create(spendEntity);

                    return SpendJson.fromEntity(createdSpendEntity);
                }
                ,
                TRANSACTION_ISOLATION_LEVEL));
    }

    public @NonNull List<SpendJson> findAllSpendsSpringJdbc() {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> {
                    List<SpendEntity> spendEntities = spendDao.findAll();
                    List<SpendJson> spendJsonList = new ArrayList<>();
                    for (SpendEntity spendEntity : spendEntities) {
                        spendJsonList.add(SpendJson.fromEntity(spendEntity));
                    }
                    return spendJsonList;
                }
                ,
                TRANSACTION_ISOLATION_LEVEL));
    }

    public @NonNull SpendJson findSpendById(@NonNull UUID spendId) {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> {
                    Optional<SpendEntity> optionalSpend = spendDao.findSpendById(spendId);
                    return optionalSpend.map(SpendJson::fromEntity).orElse(null);
                },
                TRANSACTION_ISOLATION_LEVEL));
    }

    public @NonNull List<SpendJson> findAllSpendByUsername(@NonNull String username) {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> {
                    List<SpendEntity> spendEntities = spendDao.findAllByUsername(username);
                    List<SpendJson> spendJsonList = new ArrayList<>();
                    for (SpendEntity spendEntity : spendEntities) {
                        spendJsonList.add(SpendJson.fromEntity(spendEntity));
                    }
                    return spendJsonList;
                },
                TRANSACTION_ISOLATION_LEVEL));
    }

    public void deleteSpending(@NonNull SpendJson spend) {
        jdbcTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    spendDao.deleteSpend(spendEntity);
                    return null;
                },
                TRANSACTION_ISOLATION_LEVEL);
    }
}
