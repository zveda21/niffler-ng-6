package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage extends BasePage<ProfilePage> {

  public static final String URL = CFG.frontUrl() + "profile";

  private final SelenideElement avatar = $("#image__input").parent().$("img");
  private final SelenideElement profileImage = $(".MuiAvatar-img");
  private final SelenideElement uploadANewPicButton = $(".image__input-label");
  private final SelenideElement userName = $("#username");
  private final SelenideElement nameInput = $("#name");
  private final SelenideElement photoInput = $("input[type='file']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement categoryInput = $("input[name='category']");
  private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");

  private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
  private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

  @Step("Set name: {0}")
  @Nonnull
  public ProfilePage setName(String name) {
    nameInput.clear();
    nameInput.setValue(name);
    return this;
  }

  @Step("Upload photo from classpath")
  @Nonnull
  public ProfilePage uploadPhotoFromClasspath(String path) {
    photoInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Set category: {0}")
  @Nonnull
  public ProfilePage addCategory(String category) {
    categoryInput.setValue(category).pressEnter();
    return this;
  }

  @Step("Check category: {0}")
  @Nonnull
  public ProfilePage checkCategoryExists(String category) {
    bubbles.find(text(category)).shouldBe(visible);
    return this;
  }

  @Step("Check archived category: {0}")
  @Nonnull
  public ProfilePage checkArchivedCategoryExists(String category) {
    archivedSwitcher.click();
    bubblesArchived.find(text(category)).shouldBe(visible);
    return this;
  }

  @Step("Check userName: {0}")
  @Nonnull
  public ProfilePage checkUsername(String username) {
    this.userName.should(value(username));
    return this;
  }

  @Step("Check name: {0}")
  @Nonnull
  public ProfilePage checkName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }

  @Step("Check photo")
  @Nonnull
  public ProfilePage checkPhoto(String path) throws IOException {
    final byte[] photoContent;
    try (InputStream is = new ClassPathResource(path).getInputStream()) {
      photoContent = Base64.getEncoder().encode(is.readAllBytes());
    }
    avatar.should(attribute("src", new String(photoContent, StandardCharsets.UTF_8)));
    return this;
  }

  @Step("Check photo exist")
  @Nonnull
  public ProfilePage checkPhotoExist() {
    avatar.should(attributeMatching("src", "data:image.*"));
    return this;
  }

  @Step("Check that category input is disabled")
  @Nonnull
  public ProfilePage checkThatCategoryInputDisabled() {
    categoryInput.should(disabled);
    return this;
  }

  @Step("Save profile")
  @Nonnull
  public ProfilePage submitProfile() {
    submitButton.click();
    return this;
  }

  @Step("Check that page is loaded")
  @Override
  @Nonnull
  public ProfilePage checkThatPageLoaded() {
    userName.should(visible);
    return this;
  }

  @Step("Check profile image matches the expected image")
  @Nonnull
  public ProfilePage checkProfileImage(BufferedImage expectedImage) throws IOException {
    BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(profileImage.screenshot()));
    assertFalse(new ScreenDiffResult(actualImage, expectedImage));
    return this;
  }
}
