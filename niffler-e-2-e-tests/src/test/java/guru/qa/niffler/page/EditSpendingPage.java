package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage extends BasePage<EditSpendingPage>{

    public static String spendingPageUrl = Config.getInstance().frontUrl() + "spending";

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement saveBtn = $("#save");
    private final SelenideElement addNewSpendButton = $("a[href='/spending']");

    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    public EditSpendingPage setCategory(String categoryName) {
        categoryInput.setValue(categoryName);
        return this;
    }

    public EditSpendingPage save() {
        saveBtn.click();
        return this;
    }
}
