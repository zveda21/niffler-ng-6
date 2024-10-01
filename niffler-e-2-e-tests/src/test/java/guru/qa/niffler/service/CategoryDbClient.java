package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class CategoryDbClient {

    private static final Config CFG = Config.getInstance();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    public CategoryJson createCategoryIfNotExist(CategoryJson categoryJson) {
        return transaction(connection -> {
                    Optional<CategoryEntity> existingCategory = new CategoryDaoJdbc(connection)
                            .findCategoryByUsernameAndCategoryName(categoryJson.username(), categoryJson.name());
                    if (existingCategory.isPresent()) {
                        return CategoryJson.fromEntity(existingCategory.get());
                    } else {
                        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                        return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).create(categoryEntity));
                    }
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }

    public CategoryJson findCategoryById(UUID categoryId) {
        return transaction(connection -> {
                    Optional<CategoryEntity> category = new CategoryDaoJdbc(connection).findCategoryById(categoryId);
                    return category.map(CategoryJson::fromEntity).orElse(null);
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }

    public List<CategoryJson> findAllCategoryByUsername(String username) {
        return transaction(connection -> {
                    List<CategoryEntity> categoryEntities = new CategoryDaoJdbc(connection).findAllByUsername(username);
                    List<CategoryJson> categoryJsons = new ArrayList<>();

                    for (CategoryEntity category : categoryEntities) {
                        categoryJsons.add(CategoryJson.fromEntity(category));
                    }
                    return categoryJsons;
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return transaction(connection -> {
                    Optional<CategoryEntity> categoryEntity = new CategoryDaoJdbc(connection).findCategoryByUsernameAndCategoryName(username, categoryName);
                    return Optional.of(CategoryJson.fromEntity(categoryEntity.get()));
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }

    public void deleteCategory(CategoryJson categoryJson) {
        if (categoryJson == null) {
            throw new IllegalArgumentException("CategoryJson cannot be null");
        }
        transaction(connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                    new CategoryDaoJdbc(connection).deleteCategory(categoryEntity);
                    return null;
                },
                CFG.spendJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }
}
