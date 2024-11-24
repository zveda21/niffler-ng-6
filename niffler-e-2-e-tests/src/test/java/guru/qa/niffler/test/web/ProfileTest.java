package guru.qa.niffler.test.web;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;

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
  @ApiLogin
  void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkArchivedCategoryExists(categoryName);
  }

  @User(
      categories = @Category(
          archived = false
      )
  )
  @Test
  @ApiLogin
  void activeCategoryShouldPresentInCategoriesList(UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkCategoryExists(categoryName);
  }

  @User
  @Test
  @ApiLogin
  void shouldUpdateProfileWithAllFieldsSet() {
    final String newName = randomName();
    ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
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
  @ApiLogin
  void shouldUpdateProfileWithOnlyRequiredFields() {
    final String newName = randomName();
    ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
        .setName(newName)
        .submitProfile()
        .checkAlertMessage("Profile successfully updated");

    driver.refresh();

    new ProfilePage(driver).checkName(newName);
  }

  @User
  @Test
  @ApiLogin
  void shouldAddNewCategory(UserJson user) {
    String newCategory = randomCategoryName();

    Selenide.open(ProfilePage.URL, ProfilePage.class)
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
  @ApiLogin
  void shouldForbidAddingMoreThat8Categories(UserJson user) {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkThatCategoryInputDisabled();
  }

  @User
  @ScreenShotTest(value = "img/expected_profile_image.png")
  @ApiLogin
  void checkProfileImageTest( BufferedImage expectedProfileImage) throws IOException {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
            .uploadPhotoFromClasspath("img/cat.png")
            .submitProfile()
            .checkProfileImage(expectedProfileImage);
  }
}
