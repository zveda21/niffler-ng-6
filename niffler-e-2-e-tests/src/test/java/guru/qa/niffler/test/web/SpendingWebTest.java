package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.StatComponent;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingWebTest {

  private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

  @User(
      spendings = @Spending(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990
      )
  )
  @Test
  void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
    final String newDescription = "Обучение Niffler Next Generation";

    driver.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getSpendingTable()
        .editSpending("Обучение Advanced 2.0")
        .setNewSpendingDescription(newDescription)
        .saveSpending();

    new MainPage().getSpendingTable()
        .checkTableContains(newDescription);
  }

  @User
  @Test
  void shouldAddNewSpending(UserJson user) {
    String category = "Friends";
    int amount = 100;
    Date currentDate = new Date();
    String description = RandomDataUtils.randomSentence(3);

    driver.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingCategory(category)
        .setNewSpendingAmount(amount)
        .setNewSpendingDate(currentDate)
        .setNewSpendingDescription(description)
        .saveSpending()
        .checkAlertMessage("New spending is successfully created");

    new MainPage().getSpendingTable()
        .checkTableContains(description);
  }

  @User
  @Test
  void shouldNotAddSpendingWithEmptyCategory(UserJson user) {
    driver.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingAmount(100)
        .setNewSpendingDate(new Date())
        .saveSpending()
        .checkFormErrorMessage("Please choose category");
  }

  @User
  @Test
  void shouldNotAddSpendingWithEmptyAmount(UserJson user) {
    driver.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingCategory("Friends")
        .setNewSpendingDate(new Date())
        .saveSpending()
        .checkFormErrorMessage("Amount has to be not less then 0.01");
  }

  @User(
      spendings = @Spending(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990
      )
  )
  @Test
  void deleteSpendingTest(UserJson user) {
    driver.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getSpendingTable()
        .deleteSpending("Обучение Advanced 2.0")
        .checkTableSize(0);
  }

  @User(
      spendings = @Spending(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990
      )
  )
  @ScreenShotTest("img/expected-stat.png")
  void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException, InterruptedException {
    StatComponent statComponent = driver.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent();

    Thread.sleep(3000);

    assertFalse(new ScreenDiffResult(
            expected,
            statComponent.chartScreenshot()
    ), "Screen comparison failure");

    statComponent.checkBubbles(Color.yellow);
  }

  @User(
          spendings = @Spending(
                  category = "Home",
                  description = "Test",
                  amount = 87000
          )
  )
  @Test
  void checkStatComponents(UserJson user) throws InterruptedException {
    StatComponent statComponent = driver.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent();

    Thread.sleep(3000);

    statComponent.checkBubblesInCorrectOrder(new Bubble(
            Color.yellow,
            "Home 87000 ₽"
    ));
  }

  @User(
          categories = {
                  @Category(name = "Shopping"),
                  @Category(name = "Home")
          },
          spendings = {
                  @Spending(
                          category = "Shopping",
                          description = "Test",
                          amount = 2000
                  ),
                  @Spending(
                          category = "Home",
                          description = "Test",
                          amount = 150
                  )
          }
  )
  @Test
  void checkStatBubblesInAnyOrder(UserJson user) throws InterruptedException {
    StatComponent statComponent = driver.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent();
    Thread.sleep(3000);
    Bubble bubble1 = new Bubble(Color.yellow, "Shopping 2000 ₽");
    Bubble bubble2 = new Bubble(Color.green, "Home 150 ₽");
    statComponent.checkBubblesInAnyOrder(bubble2, bubble1);

  }

  @User(
          spendings = {
                  @Spending(
                          category = "Home",
                          description = "Test",
                          amount = 67000
                  ),
                  @Spending(
                          category = "Shopping",
                          description = "Test",
                          amount = 340
                  ),
          }
  )
  @Test
  void checkStatComponentsContains(UserJson user) throws InterruptedException {
    StatComponent statComponent = driver.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent();

    Thread.sleep(3000);

    Bubble bubble_1 = new Bubble(
            Color.green,
            "Shopping 340 ₽"
    );
    statComponent.checkBubblesContains(bubble_1);
  }
}
