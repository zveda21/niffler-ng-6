package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage extends BasePage<MainPage> {

  public static final String URL = CFG.frontUrl() + "main";

  protected final Header header;
  protected final SpendingTable spendingTable;
  protected final StatComponent statComponent;

  private final SelenideElement statisticImageCanvas;
  private final ElementsCollection statisticCells;


  public MainPage(SelenideDriver driver) {
    super(driver);
    this.header = new Header(driver);
    this.spendingTable = new SpendingTable(driver);
    this.statComponent = new StatComponent(driver);
    this.statisticImageCanvas = driver.$("canvas[role='img']");
    this.statisticCells = driver.$$("#legend-container li");
  }

  @Nonnull
  public Header getHeader() {
    return header;
  }

  @Nonnull
  public StatComponent getStatComponent() {
    statComponent.getSelf().scrollIntoView(true);
    return statComponent;
  }

  @Nonnull
  public SpendingTable getSpendingTable() {
    spendingTable.getSelf().scrollIntoView(true);
    return spendingTable;
  }

  @Step("Check that page is loaded")
  @Override
  @Nonnull
  public MainPage checkThatPageLoaded() {
    header.getSelf().should(visible).shouldHave(text("Niffler"));
    statComponent.getSelf().should(visible).shouldHave(text("Statistics"));
    spendingTable.getSelf().should(visible).shouldHave(text("History of Spendings"));
    return this;
  }

  @Step("Check that statistic cells contain texts {texts}")
  @Nonnull
  public MainPage checkStatisticCellsContainText(List<String> texts) {
    for (String text : texts) {
      statisticCells.findBy(text(text)).shouldBe(visible);
    }
    return this;
  }

  @Step("Check that statistic image matches the expected image")
  @Nonnull
  public MainPage checkStatisticImage(BufferedImage expectedImage) throws IOException {
    BufferedImage actualImage = ImageIO.read(statisticImageCanvas.screenshot());
    assertFalse(new ScreenDiffResult(actualImage, expectedImage));
    return this;
  }
}
