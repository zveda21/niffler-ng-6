package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class SearchField {

    private final SelenideElement searchInput = $("input[placeholder='Search']");

    public SearchField search(String query) {
        searchInput.setValue(query);
        searchInput.pressEnter();
        return this;
    }

    public SearchField clearIfNotEmpty() {
        if (!searchInput.getValue().isEmpty()) {
            searchInput.clear();
        }
        return this;
    }
}
