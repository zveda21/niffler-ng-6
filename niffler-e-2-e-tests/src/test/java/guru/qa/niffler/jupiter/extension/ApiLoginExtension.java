package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import java.util.List;

import static guru.qa.niffler.model.rest.FriendState.*;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
  private static final Config CFG = Config.getInstance();
  private final AuthApiClient authApiClient = new AuthApiClient();
  private final boolean setupBrowser;
  private final UsersApiClient usersClient = new UsersApiClient();

  private ApiLoginExtension(boolean setupBrowser) {
    this.setupBrowser = setupBrowser;
  }

  public ApiLoginExtension() {
    this.setupBrowser = true;
  }

  public static ApiLoginExtension restApiLoginExtension() {
    return new ApiLoginExtension(false);
  }

  public static String getToken() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
  }

  public static void setToken(String token) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
  }

  public static String getCode() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
  }

  public static void setCode(String code) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
  }

  public static Cookie getJsessionIdCookie() {
    return new Cookie(
            "JSESSIONID",
            ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
    );
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
            .ifPresent(apiLogin -> {
              final UserJson userToLogin;
              final UserJson userFromUserExtension = UserExtension.getUserJson();
              if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                if (userFromUserExtension == null) {
                  throw new IllegalStateException("@User must be present in case that @ApiLogin is empty!");
                }
                userToLogin = userFromUserExtension;
              } else {
                UserJson fakeUser = new UserJson(
                        apiLogin.username(),
                        new TestData(
                                apiLogin.password()
                        )
                );
                if (userFromUserExtension != null) {
                  throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username or password!");
                }
                UserExtension.setUser(fakeUser);
                userToLogin = fakeUser;
              }
              List<UserJson> friendsList = usersClient.allUsers(userToLogin.username(), null);
              List<UserJson> friends = friendsList.stream()
                      .filter(user -> user.friendState() == FRIEND)
                      .toList();

              List<UserJson> incomeInvitations = friendsList.stream()
                      .filter(user -> user.friendState() == INVITE_RECEIVED)
                      .toList();

              List<UserJson> allUsersList = usersClient.allUsers(userToLogin.username(), null);
              List<UserJson> outcomeInvitations = allUsersList.stream()
                      .filter(user -> user.friendState() == INVITE_SENT)
                      .toList();

              TestData testData = userToLogin.testData();
              testData.friends().addAll(friends);
              testData.incomeInvitations().addAll(incomeInvitations);
              testData.outcomeInvitations().addAll(outcomeInvitations);

              SpendApiClient spendClient = new SpendApiClient();
              List<CategoryJson> categories = spendClient.getAllCategories(userToLogin.username());
              List<SpendJson> spends = spendClient.getAllSpends(userToLogin.username(), null, null, null);


              testData.categories().clear();
              testData.categories().addAll(categories);

              testData.spends().clear();
              testData.spends().addAll(spends);

              final String token = authApiClient.login(
                      userToLogin.username(),
                      userToLogin.testData().password()
              );
              setToken(token);
              if (setupBrowser) {
                Selenide.open(CFG.frontUrl());
                Selenide.localStorage().setItem("id_token", getToken());
                WebDriverRunner.getWebDriver().manage().addCookie(
                        new Cookie(
                                "JSESSIONID",
                                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
                        )
                );
                Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
              }
            });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(String.class)
            && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
  }

  @Override
  public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return getToken();
  }
}