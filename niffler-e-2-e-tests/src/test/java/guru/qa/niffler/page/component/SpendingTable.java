package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SpendingTable extends BaseComponent<SpendingTable>{

    private final SelenideElement periodButton = $("#period");
    private final SelenideElement periodList = $("ul[class^='MuiList-root']");
    private final SelenideElement spendList = $("tbody[class^='MuiTableBody-root']");
    private final SelenideElement deleteSpendButton = $("#delete");
    private SearchField searchField = new SearchField();

    public SpendingTable() {
        super($("#spendings"));
    }

    @Step("Select a period")
    public SpendingTable selectPeriod(String period) {
        String selector = String.format("li[data-value='%s']", period);
        periodButton.click();
        SelenideElement periodOption = periodList.find(selector);
        periodOption.click();
        return this;
    }

    @Step("Edit spending")
    public EditSpendingPage clickOnEditSpending(String description) {
        SelenideElement row = $$("tbody[class^='MuiTableBody-root'] tr").stream()
                .filter(tr -> tr.$("td:nth-child(4) > span").getText().equals(description))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No spending found with description: " + description));

        SelenideElement editButton = row.$("td > button");

        if (editButton.isDisplayed() && editButton.isEnabled()) {
            editButton.click();
        } else {
            throw new AssertionError("Edit button for item '" + description + "' is not clickable.");
        }
        return new EditSpendingPage();

    }

    @Step("Get spend checkbox by index")
    private SelenideElement getSpendCheckboxByIndex(int index) {
        String selector = String.format("tbody[class^='MuiTableBody-root'] tr:nth-child(%d) input", index);
        return $(selector);
    }

    @Step("Click on spend checkbox item")
    public SpendingTable clickOnSpendCheckboxItem(int index) {
        SelenideElement spendInput = getSpendCheckboxByIndex(index);
        spendInput.click();
        clickOnDeleteButton();
        return this;
    }

    @Step("Delete a spend")
    public SpendingTable deleteSpending(String description) {
        String selector = String.format("tbody[class^='MuiTableBody-root'] td:nth-child(4) span:contains('%s')", description);
        SelenideElement itemToClick = $(selector);
        itemToClick.click();
        return this;
    }

    @Step("Click on delete button")
    public void clickOnDeleteButton() {
        if (deleteSpendButton.isDisplayed() && deleteSpendButton.isEnabled()) {
            deleteSpendButton.click();
        } else {
            throw new IllegalStateException("Delete button is not clickable.");
        }
    }

    @Step("Search a spend by description")
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }


    @Step("Check if table contains")
    public SpendingTable checkTableContains(String... expectedSpends) {
        ElementsCollection rows = spendList.$$("tr");
        for (String expectedSpend : expectedSpends) {
            boolean found = false;
            for (SelenideElement row : rows) {
                String actualSpend = row.$$("td").get(3).getText();
                if (actualSpend.contains(expectedSpend)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new AssertionError("Expected spend not found in the table: " + expectedSpend);
            }
        }
        return this;

    }
}
