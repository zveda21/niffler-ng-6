package guru.qa.niffler.page;

import guru.qa.niffler.config.Config;

public class PeoplePage extends BasePage<PeoplePage> {

    public static String url = Config.getInstance().frontUrl() + "people/all";

}
