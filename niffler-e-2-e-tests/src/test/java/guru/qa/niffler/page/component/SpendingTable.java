package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.page.EditSpendingPage;

import static com.codeborne.selenide.Selenide.$;

public class SpendingTable {

    private final SelenideElement periodList = $("ul[class^='MuiList-root']");


    public SpendingTable selectPeriod(DataFilterValues period) {
        String selector = String.format("li[data-value='%s']", period);
        SelenideElement periodOption = periodList.find(selector);
        periodOption.click();
        return this;
    }

    public EditSpendingPage editSpending(String description) {

    }
}
