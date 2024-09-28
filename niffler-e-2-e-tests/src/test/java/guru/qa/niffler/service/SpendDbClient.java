package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        Optional<CategoryEntity> existingCategory = categoryDao.findCategoryById(spendEntity.getCategory().getId());
        if (existingCategory.isPresent()) {
            spendEntity.setCategory(existingCategory.get());
        } else {
            CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(
                spendDao.create(spendEntity)
        );
    }

    public SpendJson findSpendById(UUID spendId) {
        Optional<SpendEntity> optionalSpend = spendDao.findSpendById(spendId);
        return optionalSpend.map(SpendJson::fromEntity).orElse(null);
    }

    public List<SpendJson> findAllSpendByUsername(String username) {
        List<SpendEntity> spendEntities = spendDao.findAllByUsername(username);
        List<SpendJson> spendJsonList = new ArrayList<>();

        for (SpendEntity spendEntity : spendEntities) {
            spendJsonList.add(SpendJson.fromEntity(spendEntity));
        }
        return spendJsonList;
    }

    public void deleteSpending(SpendJson spend) {
        if (spend == null) {
            throw new IllegalArgumentException("SpendJson cannot be null");
        }
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        spendDao.deleteSpend(spendEntity);
    }
}
