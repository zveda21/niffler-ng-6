package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

public class Calendar {

    private final SelenideElement calendarInput = $("input[name='date']");

    @Step("Select date in calendar")
    public Calendar selectDateInCalendar(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = dateFormat.format(date);

        calendarInput.clear();
        calendarInput.setValue(formattedDate);
        calendarInput.pressEnter();

        return this;
    }
}
