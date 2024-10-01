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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
    private final SpendDao spendDao = new SpendDaoSpringJdbc();
    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());

    public SpendJson createSpend(SpendJson spend) {
        return jdbcTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendDao.create(spendEntity));
                },
                TRANSACTION_ISOLATION_LEVEL
        );
    }

    public SpendJson createSpendUsingSpringJdbc(SpendJson spendJson) {
        return jdbcTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    SpendEntity createdSpendEntity = spendDao.create(spendEntity);

                    return SpendJson.fromEntity(createdSpendEntity);
                }
                ,
                TRANSACTION_ISOLATION_LEVEL);
    }

    public List<SpendJson> findAllSpendsSpringJdbc() {
        return jdbcTemplate.execute(() -> {
                    List<SpendEntity> spendEntities = spendDao.findAll();
                    List<SpendJson> spendJsonList = new ArrayList<>();
                    for (SpendEntity spendEntity : spendEntities) {
                        spendJsonList.add(SpendJson.fromEntity(spendEntity));
                    }
                    return spendJsonList;
                }
                ,
                TRANSACTION_ISOLATION_LEVEL);
    }

    public SpendJson findSpendById(UUID spendId) {
        return jdbcTemplate.execute(() -> {
                    Optional<SpendEntity> optionalSpend = spendDao.findSpendById(spendId);
                    return optionalSpend.map(SpendJson::fromEntity).orElse(null);
                },
                TRANSACTION_ISOLATION_LEVEL);
    }

    public List<SpendJson> findAllSpendByUsername(String username) {
        return jdbcTemplate.execute(() -> {
                    List<SpendEntity> spendEntities = spendDao.findAllByUsername(username);
                    List<SpendJson> spendJsonList = new ArrayList<>();
                    for (SpendEntity spendEntity : spendEntities) {
                        spendJsonList.add(SpendJson.fromEntity(spendEntity));
                    }
                    return spendJsonList;
                },
                TRANSACTION_ISOLATION_LEVEL);
    }

    public void deleteSpending(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("SpendJson cannot be null");
        }
        jdbcTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    spendDao.deleteSpend(spendEntity);
                    return null;
                },
                TRANSACTION_ISOLATION_LEVEL);
    }
}
