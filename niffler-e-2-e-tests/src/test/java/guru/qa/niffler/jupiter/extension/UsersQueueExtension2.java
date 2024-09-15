//package guru.qa.niffler.jupiter.extension;
//
//import io.qameta.allure.Allure;
//import org.apache.commons.lang3.time.StopWatch;
//import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
//import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
//import org.junit.jupiter.api.extension.ExtensionContext;
//import org.junit.jupiter.api.extension.ParameterContext;
//import org.junit.jupiter.api.extension.ParameterResolutionException;
//import org.junit.jupiter.api.extension.ParameterResolver;
//import org.junit.platform.commons.support.AnnotationSupport;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//import java.util.*;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.TimeUnit;
//
//public class UsersQueueExtension2 implements
//        BeforeTestExecutionCallback,
//        AfterTestExecutionCallback,
//        ParameterResolver {
//
//    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension2.class);
//
//    public record StaticUser(String username,
//                             String password,
//                             String friend,
//                             String income,
//                             String outcome) {
//    }
//
//    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
//    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
//    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
//    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
//
//    static {
//        EMPTY_USERS.add(new StaticUser("bee", "12345", null, null, null));
//        WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "dima", null, null));
//        WITH_INCOME_REQUEST_USERS.add(new StaticUser("dima", "12345", null, "bee", null));
//        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("barsik", "12345", null, null, "bill"));
//    }
//
//    @Target(ElementType.PARAMETER)
//    @Retention(RetentionPolicy.RUNTIME)
//    public @interface UserType {
//
//        Type value() default Type.EMPTY;
//
//        enum Type {
//            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
//        }
//
//    }
//
//    @Override
//    public void beforeTestExecution(ExtensionContext context) {
//        Map<UserType.Type, StaticUser> userMap = (Map<UserType.Type, StaticUser>) context.getStore(NAMESPACE)
//                .getOrComputeIfAbsent(context.getUniqueId(), key -> new HashMap<>());
//
//        Arrays.stream(context.getRequiredTestMethod().getParameters())
//                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
//                .forEach(p -> {
//                    UserType userType = p.getAnnotation(UserType.class);
//                    StaticUser user = fetchUser(userType.value());
//
//                    if (user == null) {
//                        throw new IllegalStateException("Cannot obtain user of type " + userType.value() + " after 30 seconds.");
//                    }
//
//                    userMap.put(userType.value(), user);
//                });
//
//        context.getStore(NAMESPACE).put(context.getUniqueId(), userMap);
//
//    }
//
//    private StaticUser fetchUser(UserType.Type userType) {
//        Optional<StaticUser> userOptional = Optional.empty();
//        StopWatch sw = StopWatch.createStarted();
//
//        while (userOptional.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
//            userOptional = switch (userType) {
//                case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
//                case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
//                case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
//                case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
//                default -> throw new IllegalArgumentException("Unsupported UserType: " + userType);
//            };
//        }
//
//        return userOptional.orElse(null);
//    }
//
//
//    @Override
//    public void afterTestExecution(ExtensionContext context) {
//        StaticUser user = context.getStore(NAMESPACE).get(
//                context.getUniqueId(),
//                StaticUser.class
//        );
//
//    }
//
//    @Override
//    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
//        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
//                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
//    }
//
//    @Override
//    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
//        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), StaticUser.class);
//    }
//}
//
