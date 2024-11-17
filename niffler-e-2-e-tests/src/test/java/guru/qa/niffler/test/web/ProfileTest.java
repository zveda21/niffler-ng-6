package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {

  private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

  @User(
      categories = @Category(
          archived = true
      )
  )
  @Test
  void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage(driver))
        .checkThatPageLoaded();

    driver.open(ProfilePage.URL);
    new ProfilePage(driver)
        .checkArchivedCategoryExists(categoryName);
  }

  @User(
      categories = @Category(
          archived = false
      )
  )
  @Test
  void activeCategoryShouldPresentInCategoriesList(UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage(driver))
        .checkThatPageLoaded();

    driver.open(ProfilePage.URL);
    new ProfilePage(driver)
        .checkCategoryExists(categoryName);
  }

  @User
  @Test
  void shouldUpdateProfileWithAllFieldsSet(UserJson user) {
    final String newName = randomName();

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage(driver))
        .checkThatPageLoaded()
        .getHeader()
        .toProfilePage()
        .uploadPhotoFromClasspath("img/cat.png")
        .setName(newName)
        .submitProfile()
        .checkAlertMessage("Profile successfully updated");

    driver.refresh();

    new ProfilePage(driver).checkName(newName)
        .checkPhotoExist();
  }

  @User
  @Test
  void shouldUpdateProfileWithOnlyRequiredFields(UserJson user) {
    final String newName = randomName();

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage(driver))
        .checkThatPageLoaded()
        .getHeader()
        .toProfilePage()
        .setName(newName)
        .submitProfile()
        .checkAlertMessage("Profile successfully updated");

    driver.refresh();

    new ProfilePage(driver).checkName(newName);
  }

  @User
  @Test
  void shouldAddNewCategory(UserJson user) {
    String newCategory = randomCategoryName();

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage(driver))
        .checkThatPageLoaded()
        .getHeader()
        .toProfilePage()
        .addCategory(newCategory)
        .checkAlertMessage("You've added new category:")
        .checkCategoryExists(newCategory);
  }

  @User(
      categories = {
          @Category(name = "Food"),
          @Category(name = "Bars"),
          @Category(name = "Clothes"),
          @Category(name = "Friends"),
          @Category(name = "Music"),
          @Category(name = "Sports"),
          @Category(name = "Walks"),
          @Category(name = "Books")
      }
  )
  @Test
  void shouldForbidAddingMoreThat8Categories(UserJson user) {
    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage(driver))
        .checkThatPageLoaded()
        .getHeader()
        .toProfilePage()
        .checkThatCategoryInputDisabled();
  }

  @User
  @ScreenShotTest(value = "img/expected_profile_image.png")
  void checkProfileImageTest(UserJson user, BufferedImage expectedProfileImage) throws IOException {
    driver.open(LoginPage.URL);
    new LoginPage(driver)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage(driver))
            .checkThatPageLoaded()
            .getHeader()
            .toProfilePage()
            .uploadPhotoFromClasspath("img/cat.png")
            .submitProfile()
            .checkProfileImage(expectedProfileImage);
  }
}
