package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    private CategoryService categoryService;

    @Test
    void testGetAllCategoriesFiltersArchivedCategories(@Mock CategoryRepository categoryRepository) {
        String username= "Zveda";

        when(categoryRepository.findAllByUsernameOrderByName(eq(username))).thenReturn(getMockCategories(username));

         categoryService = new CategoryService(categoryRepository);

        List<CategoryJson> result = categoryService.getAllCategories(username, true);

        // Asserts
        assertEquals(1, result.size(), "The list should contain only non-archived categories.");
        assertTrue(result.stream().noneMatch(category -> category.archived()), "All returned categories should be non-archived.");
    }

    @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
    @ParameterizedTest
    void categoryNameArchivedShouldBeDenied(String categoryName, @Mock CategoryRepository categoryRepository) {
        final String username = "testZi";
        final UUID id = UUID.randomUUID();
        final CategoryEntity shoppingCategory = new CategoryEntity();
        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        shoppingCategory
                ));
        categoryService = new CategoryService(categoryRepository);
        CategoryJson categoryJson = new CategoryJson(
                id,
                categoryName,
                username,
                true
        );
        InvalidCategoryNameException ex = Assertions.assertThrows(
                InvalidCategoryNameException.class,
                () -> categoryService.update(categoryJson)
        );

        //Assert
        Assertions.assertEquals(
                "Can`t add category with name: '" + categoryName + "'",
                ex.getMessage()
        );
    }

    @Test
    void testSaveCategoryDoesNotExceedMaxCategoriesSize(@Mock CategoryRepository categoryRepository) {
        final String username = "zveda";
        final long MAX_CATEGORIES_SIZE = 8;

        when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(MAX_CATEGORIES_SIZE);

        CategoryService categoryService = new CategoryService(categoryRepository);
        CategoryJson newCategory = new CategoryJson(null, "Category_9", username, false);

        TooManyCategoriesException ex = assertThrows(
                TooManyCategoriesException.class,
                () -> categoryService.save(newCategory)
        );

        //Assert
        assertEquals("Can`t add over than " + MAX_CATEGORIES_SIZE + " categories for user: '" + username + "'", ex.getMessage());
    }

    @Test
    void updateShouldSaveUpdatedCategory(@Mock CategoryRepository categoryRepository) {
        final String username = "zveda";
        final UUID categoryId = UUID.randomUUID();

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(categoryId);
        existingCategory.setName("Existing");
        existingCategory.setUsername(username);
        existingCategory.setArchived(true);

        when(categoryRepository.findByUsernameAndId(eq(username), eq(categoryId))).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(3L);
        when(categoryRepository.save(any(CategoryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        categoryService = new CategoryService(categoryRepository);
        CategoryJson categoryToUpdate = new CategoryJson(categoryId, "UpdatedCategory", username, false);

        CategoryJson result = categoryService.update(categoryToUpdate);

        //Asserts
        assertEquals("UpdatedCategory", result.name());
        assertFalse(result.archived());
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
        final String username = "zveda";
        final UUID id = UUID.randomUUID();

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.empty());

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "",
                username,
                true
        );

        CategoryNotFoundException ex = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(categoryJson)
        );

        //Assert
        Assertions.assertEquals(
                "Can`t find category by id: '" + id + "'",
                ex.getMessage()
        );
    }

    private List<CategoryEntity> getMockCategories(String username) {
        CategoryEntity categoryEntity1 = new CategoryEntity();
        categoryEntity1.setId(UUID.randomUUID());
        categoryEntity1.setName("category1");
        categoryEntity1.setUsername(username);
        categoryEntity1.setArchived(true);

        CategoryEntity categoryEntity2 = new CategoryEntity();
        categoryEntity2.setId(UUID.randomUUID());
        categoryEntity2.setName("category2");
        categoryEntity2.setUsername(username);
        categoryEntity2.setArchived(false);

        return List.of(categoryEntity1, categoryEntity2);
    }
}
