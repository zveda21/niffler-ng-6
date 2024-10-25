package guru.qa.niffler.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import jaxb.userdata.FriendState;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("fullname")
        String fullname,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("photoSmall")
        String photoSmall,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("username")
        String username,
        @JsonProperty("friendState")
        FriendState friendState,
        @JsonIgnore
        TestData testData) {
    public static UserJson fromEntity(UserEntity entity, FriendState friendState) {
        return new UserJson(
                entity.getId(),
                entity.getCurrency(),
                entity.getFirstname(),
                entity.getFullname(),
                entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
                entity.getPhotoSmall() != null && entity.getPhotoSmall().length > 0 ? new String(entity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
                entity.getSurname(),
                entity.getUsername(),
                friendState,
                null
        );
    }

    public UserJson addTestData(TestData testData) {
        return new UserJson(
                id, currency, firstname, fullname, photo, photoSmall, surname, username, friendState, testData
        );
    }
}