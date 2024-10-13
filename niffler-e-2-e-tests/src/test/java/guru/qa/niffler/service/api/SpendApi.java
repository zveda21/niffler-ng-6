package guru.qa.niffler.service.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SpendApi {

    @POST("/spend")
    Call<SpendJson> createSpend(@Body SpendJson spend);

    @POST("/category")
    Call<CategoryJson> createCategory(@Body CategoryJson category);

    @DELETE("/category/{id}")
    Call<Void> removeCategory(@Path("id") String categoryId);
}
