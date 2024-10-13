package guru.qa.niffler.service.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SpendClientImpl implements SpendApi {

    private final SpendApi spendApi;

    public SpendClientImpl(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        this.spendApi = retrofit.create(SpendApi.class);
    }

    @Override
    public Call<SpendJson> createSpend(SpendJson spend) {
        return spendApi.createSpend(spend);
    }

    @Override
    public Call<CategoryJson> createCategory(CategoryJson category) {
        return spendApi.createCategory(category);
    }

    @Override
    public Call<Void> removeCategory(String categoryId) {
        return spendApi.removeCategory(categoryId);
    }
}
