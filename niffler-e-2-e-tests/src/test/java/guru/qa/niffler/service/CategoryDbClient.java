package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDbClient {

    private final CategoryDao categoryDao = new CategoryDaoJdbc();


    public CategoryJson createCategory(CategoryJson categoryJson) {
        Optional<CategoryEntity> existingCategory = categoryDao.findCategoryByUsernameAndCategoryName(categoryJson.username(), categoryJson.name());
        if (existingCategory.isPresent()) {
            return CategoryJson.fromEntity(existingCategory.get());
        } else {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
        }
    }

    public CategoryJson findCategoryById(UUID categoryId) {
        Optional<CategoryEntity> category = categoryDao.findCategoryById(categoryId);
        return category.map(CategoryJson::fromEntity).orElse(null);
    }

    public List<CategoryJson> findAllCategoryByUsername(String username) {
        List<CategoryEntity> categoryEntities = categoryDao.findAllByUsername(username);
        List<CategoryJson> categoryJsons = new ArrayList<>();

        for (CategoryEntity category : categoryEntities) {
            categoryJsons.add(CategoryJson.fromEntity(category));
        }
        return categoryJsons;
    }

    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
    }

    public void deleteSpending(CategoryJson categoryJson) {
        if (categoryJson == null) {
            throw new IllegalArgumentException("CategoryJson cannot be null");
        }
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        categoryDao.deleteCategory(categoryEntity);
    }
}
