package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

  @GET("register")
  Call<Void> requestRegisterForm();

  @POST("oauth2/token")
  @FormUrlEncoded
  Call<String> token(
      @Field("client_id") String clientId,
      @Field(value = "redirect_uri", encoded = true) String redirectUri,
      @Field("grant_type") String grantType,
      @Field("code") String code,
      @Field("code_verifier") String codeVerifier);

  @POST("login")
  @FormUrlEncoded
  Call<String> login(
      @Field("username") String username,
      @Field("password") String password,
      @Field("_csrf") String csrf);

  @POST("register")
  @FormUrlEncoded
  Call<Void> register(
      @Field("username") String username,
      @Field("password") String password,
      @Field("passwordSubmit") String passwordSubmit,
      @Field("_csrf") String csrf);
}
