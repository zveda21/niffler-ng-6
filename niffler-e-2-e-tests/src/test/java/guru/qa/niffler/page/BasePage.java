package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

  protected static final Config CFG = Config.getInstance();

//  private final SelenideElement alert = $(".MuiSnackbar-root");
//  private final ElementsCollection formErrors = $$("p.Mui-error, .input__helper-text");

  private final SelenideElement alert;
  private final ElementsCollection formErrors;

  protected BasePage(SelenideDriver driver) {
    this.alert = driver.$(".MuiSnackbar-root");
    this.formErrors = driver.$$("p.Mui-error, .input__helper-text");
  }

  public BasePage() {
    this.alert = Selenide.$(".MuiSnackbar-root");
    this.formErrors = Selenide.$$("p.Mui-error, .input__helper-text");
  }

  public abstract T checkThatPageLoaded();

  @Step("Check that alert message appears: {expectedText}")
  @SuppressWarnings("unchecked")
  @Nonnull
  public T checkAlertMessage(String expectedText) {
    alert.should(Condition.visible).should(Condition.text(expectedText));
    return (T) this;
  }

  @Step("Check that form error message appears: {expectedText}")
  @SuppressWarnings("unchecked")
  @Nonnull
  public T checkFormErrorMessage(String... expectedText) {
    formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
    return (T) this;
  }
}
