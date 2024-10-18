package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.SpendingTable;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "zveda",
            categories = @Category(
                    archived = true
            ),
            spendings = @Spending(
                    category = "Обучение 28",
                    description = "Обучение Advanced 2.1",
                    amount = 65990
            )
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(SpendJson spend) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(spend.username(), "12345");
        new MainPage()
                .editSpending(spend.description())
                .setNewSpendingDescription(newDescription)
                .save();

        new MainPage().checkThatTableContainsSpending(newDescription);
    }

    @Test
    public void checkIfTableContainsSpendTest() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("vates5", "12345");
        new SpendingTable()
                .clickOnSpendCheckboxItem(1)
                .checkTableContains("test");
    }

    @User(
            username = "zveda",
            spendings = @Spending(
                    category = "Обучение 42",
                    description = "Обучение Advanced 42",
                    amount = 65990
            )
    )
    @Test
    void searchSpendByDescriptionTest(SpendJson spendJson) {

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(spendJson.username(), "12345");

        new SpendingTable()
                .searchSpendingByDescription(spendJson.description())
                .selectPeriod("TODAY")
                .checkTableContains(spendJson.description());
    }

    @SneakyThrows
    @User(
            username = "vates",
            spendings = @Spending(
                    category = "13",
                    description = "Test2",
                    amount = 2345
            )
    )
    @Test
    void editSpendTest(SpendJson spendJson) {

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(spendJson.username(), "12345");

        new SpendingTable()
                .searchSpendingByDescription(spendJson.description())
                .selectPeriod("TODAY")
                .clickOnEditSpending(spendJson.description());
        new EditSpendingPage().setCategory("test")
                .setNewSpendingDescription("new desc")
                .save();
        Thread.sleep(4000);
    }
}
