package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class SearchField {

    private final SelenideElement searchInput = $("input[placeholder='Search']");

    @Step("Perform a search")
    public SearchField search(String query) {
        searchInput.setValue(query);
        searchInput.pressEnter();
        return this;
    }

    @Step("Clear search field")
    public SearchField clearSearchField() {
        searchInput.clear();
        return this;
    }
}
