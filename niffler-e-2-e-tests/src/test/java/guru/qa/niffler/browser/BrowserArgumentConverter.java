package guru.qa.niffler.browser;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserArgumentConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (source instanceof Browser ) {
            Browser browser = (Browser) source;
            SelenideConfig config = switch (browser) {
                case CHROME -> SelenideUtils.chromeConfig;
                case FIREFOX -> SelenideUtils.firefoxConfig;
                default -> throw new ArgumentConversionException("Unsupported browser: " + browser);
            };

            return new SelenideDriver(config);
        }
        throw new ArgumentConversionException("Cannot convert source: " + source);
    }
}
