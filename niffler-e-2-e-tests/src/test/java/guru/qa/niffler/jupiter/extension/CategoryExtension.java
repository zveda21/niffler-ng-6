package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.CategoryDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements
        BeforeEachCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    private final CategoryDbClient categoryDbClient = new CategoryDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    if (anno.categories().length > 0) {
                        Category category = anno.categories()[0];
                        CategoryJson categoryJson = new CategoryJson(
                                null,
                                RandomDataUtils.randomName(),
                                anno.username(),
                                false
                        );

                        CategoryJson created = categoryDbClient.createCategoryIfNotExist(categoryJson);
                        if (category.archived()) {
                            CategoryJson archivedCategory = new CategoryJson(
                                    created.id(),
                                    created.name(),
                                    created.username(),
                                    true
                            );
                            created = categoryDbClient.createCategoryIfNotExist(archivedCategory);
                        }

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                created
                        );
                    }
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (category != null) {
            if (!category.archived()) {
                category = new CategoryJson(
                        category.id(),
                        category.name(),
                        category.username(),
                        true
                );
                categoryDbClient.deleteCategory(category);
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
