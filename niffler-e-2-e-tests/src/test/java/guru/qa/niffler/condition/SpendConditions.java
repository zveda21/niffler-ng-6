package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.rest.SpendJson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class SpendConditions {

    @Nonnull
    public static WebElementsCondition spends(@Nonnull guru.qa.niffler.model.rest.SpendJson... expectedSpends) {
        return new WebElementsCondition() {

            final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedSpends.length != elements.size()) {
                    return CheckResult.rejected(String.format("Mismatch in table rows (expected: %d, actual: %d)", expectedSpends.length, elements.size()), elements);
                }

                List<String> actualSpends = new ArrayList<>();
                boolean isValid = true;

                for (int i = 0; i < elements.size(); i++) {
                    WebElement row = elements.get(i);
                    List<WebElement> cells = row.findElements(By.cssSelector("td"));

                    SpendJson expectedSpend = expectedSpends[i];
                    String category = cells.get(1).getText().trim();
                    String amount = cells.get(2).getText().split(" ")[0].trim();
                    String description = cells.get(3).getText().trim();
                    String actualDate = formatDate(cells.get(4).getText().trim());

                    actualSpends.add(String.format("Category: %s, Amount: %s, Description: %s, Date: %s", category, amount, description, actualDate));

                    if (!category.equals(expectedSpend.category().name()) ||
                            !amount.equals(String.format("%.0f", expectedSpend.amount())) ||
                            !description.equals(expectedSpend.description()) ||
                            !actualDate.equals(dateFormatter.format(expectedSpend.spendDate()))) {
                        isValid = false;
                    }
                }

                if (!isValid) {
                    return rejected("Mismatch in spend data", actualSpends);
                }
                return accepted();
            }

            private String formatDate(String dateStr) {
                try {
                    Date parsedDate = dateFormatter.parse(dateStr);
                    return dateFormatter.format(parsedDate);
                } catch (ParseException e) {
                    return "Invalid Date Format";
                }
            }

            @Override
            public String toString() {
                return "Spend Condition Check";
            }
        };
    }
}