package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

  @Nonnull
  public static WebElementCondition color(Color expectedColor) {
    return new WebElementCondition("color " + expectedColor.rgb) {
      @NotNull
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        final String rgba = element.getCssValue("background-color");
        return new CheckResult(
            expectedColor.rgb.equals(rgba),
            rgba
        );
      }
    };
  }

  @Nonnull
  public static WebElementsCondition color(@Nonnull Color... expectedColors) {
    return new WebElementsCondition() {

      private final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedColors)) {
          throw new IllegalArgumentException("No expected colors given");
        }
        if (expectedColors.length != elements.size()) {
          final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedColors.length, elements.size());
          return rejected(message, elements);
        }

        boolean passed = true;
        final List<String> actualRgbaList = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
          final WebElement elementToCheck = elements.get(i);
          final Color colorToCheck = expectedColors[i];
          final String rgba = elementToCheck.getCssValue("background-color");
          actualRgbaList.add(rgba);
          if (passed) {
            passed = colorToCheck.rgb.equals(rgba);
          }
        }

        if (!passed) {
          final String actualRgba = actualRgbaList.toString();
          final String message = String.format(
              "List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba
          );
          return rejected(message, actualRgba);
        }
        return accepted();
      }

      @Override
      public String toString() {
        return expectedRgba;
      }
    };
  }

  @Nonnull
  public static WebElementsCondition statBubbles(@Nonnull Bubble... expectedBubbles) {
    return new WebElementsCondition() {

      private final String expectedValues = Arrays.stream(expectedBubbles)
              .map(b -> b.color().rgb + " " + b.text())
              .toList().toString();

      @Nonnull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedBubbles)) {
          throw new IllegalArgumentException("No expected colors given");
        }
        if (expectedBubbles.length != elements.size()) {
          String message = String.format("Number of elements mismatch (expected: %d, actual: %d)", expectedBubbles.length, elements.size());
          return CheckResult.rejected(message, elements);
        }

        boolean passed = true;
        final List<String> actualValuesList = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
          final WebElement elementToCheck = elements.get(i);
          final Bubble expectedBubble = expectedBubbles[i];
          final String actualColor = elementToCheck.getCssValue("background-color");
          final String actualText = elementToCheck.getText();
          actualValuesList.add(actualColor + " " + actualText);

          if (passed) {
            passed = expectedBubble.color().rgb.equals(actualColor) && expectedBubble.text().equals(actualText);
          }
        }
        if (!passed) {
          final String actualValues = actualValuesList.toString();
          final String message = String.format(
                  "List bubbles mismatch (expected: %s, actual: %s)", expectedValues, actualValues
          );
          return rejected(message, actualValues);
        }
        return accepted();
      }

      @Override
      public String toString() {
        return "Checking statBubbles with expected values.";
      }
    };
  }

  @Nonnull
  public static WebElementsCondition statBubblesInAnyOrder(@Nonnull Bubble... expectedBubbles) {
    return new WebElementsCondition() {

      private final String expectedValues = Arrays.stream(expectedBubbles)
              .map(b -> b.color().rgb + " " + b.text())
              .toList().toString();

      @Nonnull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedBubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }
        if (expectedBubbles.length != elements.size()) {
          String message = String.format("Number of elements mismatch (expected: %d, actual: %d)", expectedBubbles.length, elements.size());
          return CheckResult.rejected(message, elements);
        }

        List<String> expectedValuesList = Arrays.stream(expectedBubbles)
                .map(b -> b.color().rgb + " " + b.text())
                .toList();
        List<String> actualValuesList = new ArrayList<>();
        for (WebElement element : elements) {
          final String actualColor = element.getCssValue("background-color");
          final String actualText = element.getText();
          actualValuesList.add(actualColor + " " + actualText);
        }

        if (actualValuesList.containsAll(expectedValuesList) && expectedValuesList.containsAll(actualValuesList)) {
          return accepted();
        } else {
          final String actualValues = actualValuesList.toString();
          final String message = String.format(
                  "List bubbles mismatch (expected: %s, actual: %s)", expectedValues, actualValues
          );
          return rejected(message, actualValues);
        }
      }

      @Override
      public String toString() {
        return "Checking statBubbles in any order with expected values.";
      }
    };
  }

  @Nonnull
  public static WebElementsCondition statBubblesContains(@Nonnull Bubble... expectedBubbles) {
    return new WebElementsCondition() {
      private final List<String> expectedList = Arrays.stream(expectedBubbles)
              .map(b -> b.color().rgb + ": " + b.text()).toList();
      private final String expected = expectedList.toString();

      @Nonnull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (ArrayUtils.isEmpty(expectedBubbles)) {
          throw new IllegalArgumentException("No expected bubbles given");
        }

        List<String> actualList = new ArrayList<>();
        for (WebElement e : elements) {
          final String actualText = e.getText();
          final String actualColor = e.getCssValue("background-color");
          actualList.add(actualColor + ": " + actualText);
        }

        if (actualList.containsAll(expectedList)) {
          return accepted();
        } else {
          final String actual = actualList.toString();
          final String message = String.format(
                  "List bubbles mismatch (expected: %s, actual: %s)", expected, actual
          );
          return rejected(message, actual);
        }
      }

      @Override
      public String toString() {
        return "Checking if expected bubbles are present in the list of actual bubbles.";
      }
    };
  }
}
