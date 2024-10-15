package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {

    public static String spendingPageUrl = Config.getInstance().frontUrl() + "spending";

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveBtn = $("#save");
    private final SelenideElement addNewSpendButton = $("a[href='/spending']");

    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    public void save() {
        saveBtn.click();
    }
}
