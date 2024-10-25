package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import lombok.NonNull;

import java.sql.Connection;
import java.util.*;


public class CategoryDbClient {

    private static final Config CFG = Config.getInstance();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
    private final JdbcTransactionTemplate jdbcTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());
    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    public CategoryJson createCategoryIfNotExist(@NonNull CategoryJson categoryJson) {
        return jdbcTemplate.execute(() -> {
                    Optional<CategoryEntity> existingCategory = categoryDao.findCategoryByUsernameAndCategoryName(categoryJson.username(), categoryJson.name());
                    if (existingCategory.isPresent()) {
                        return CategoryJson.fromEntity(existingCategory.get());
                    } else {
                        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                        return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
                    }
                },
                TRANSACTION_ISOLATION_LEVEL);

    }

    public CategoryJson findCategoryById(@NonNull UUID categoryId) {
        return jdbcTemplate.execute(() -> {
                    Optional<CategoryEntity> category = categoryDao.findCategoryById(categoryId);
                    return category.map(CategoryJson::fromEntity).orElse(null);
                },
                TRANSACTION_ISOLATION_LEVEL);
    }

    public List<CategoryJson> findAllCategoryByUsername(@NonNull String username) {
        return jdbcTemplate.execute(() -> {
                    List<CategoryEntity> categoryEntities = categoryDao.findAllByUsername(username);
                    List<CategoryJson> categoryJsons = new ArrayList<>();

                    for (CategoryEntity category : categoryEntities) {
                        categoryJsons.add(CategoryJson.fromEntity(category));
                    }
                    return categoryJsons;
                },
                TRANSACTION_ISOLATION_LEVEL);
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(@NonNull String username,@NonNull String categoryName) {
        return jdbcTemplate.execute(() -> {
                    Optional<CategoryEntity> categoryEntity = categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
                    return Optional.of(CategoryJson.fromEntity(categoryEntity.get()));
                },
                TRANSACTION_ISOLATION_LEVEL);
    }

    public void deleteCategory(@NonNull CategoryJson categoryJson) {
        jdbcTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                    categoryDao.deleteCategory(categoryEntity);
                    return null;
                },
                TRANSACTION_ISOLATION_LEVEL);
    }

    public @NonNull List<CategoryJson> findAllCategories() {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> {
                    List<CategoryEntity> categoryEntities = categoryDao.findAll();
                    List<CategoryJson> categoryJsonList = new ArrayList<>();
                    for (CategoryEntity categoryEntity : categoryEntities) {
                        categoryJsonList.add(CategoryJson.fromEntity(categoryEntity));
                    }
                    return categoryJsonList;
                },
                TRANSACTION_ISOLATION_LEVEL));
    }
}
