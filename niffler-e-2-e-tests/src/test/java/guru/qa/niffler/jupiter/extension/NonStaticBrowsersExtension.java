package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import guru.qa.niffler.utils.SelenideUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;

public class NonStaticBrowsersExtension implements BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private static final ThreadLocal<SelenideDriver> driverThreadLocal = ThreadLocal.withInitial(() -> new SelenideDriver(SelenideUtils.chromeConfig));

    public static SelenideDriver getDriver() {
        return driverThreadLocal.get();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );
        driverThreadLocal.set(new SelenideDriver(SelenideUtils.chromeConfig));
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        SelenideDriver driver = driverThreadLocal.get();
        if (driver != null && driver.hasWebDriverStarted()) {
            driver.close();
        }
        driverThreadLocal.remove();
    }

    private void doScreenshot() {
        SelenideDriver driver = driverThreadLocal.get();
        if (driver != null && driver.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail",
                    new ByteArrayInputStream(
                            ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
    }
}
