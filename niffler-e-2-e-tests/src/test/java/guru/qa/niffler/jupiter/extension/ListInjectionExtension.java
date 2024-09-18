package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.ListInject;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

public class ListInjectionExtension implements ParameterResolver
        , BeforeEachCallback
{

    private static final List<String> STRING_LIST = Arrays.asList("apple","banana","strawberry");

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, ListInject.class))
                .forEach(p -> {
                    System.out.println("p---"+p.toString());
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(ListInject.class) &&
                parameterContext.getParameter().getType().equals(List.class) &&
                parameterContext.getParameter().getParameterizedType() instanceof ParameterizedType &&
                ((ParameterizedType) parameterContext.getParameter().getParameterizedType()).getActualTypeArguments()[0].equals(String.class);

    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (parameterContext.isAnnotated(ListInject.class)) {
            return STRING_LIST;
        }
        throw new ParameterResolutionException("Parameter not annotated with @ListInject");
    }
}
