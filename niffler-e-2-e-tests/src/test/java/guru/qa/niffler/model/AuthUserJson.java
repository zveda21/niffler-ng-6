package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import lombok.NonNull;

import java.util.UUID;

public record AuthUserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("account_non_expired")
        Boolean accountNonExpired,
        @JsonProperty("account_non_locked")
        Boolean accountNonLocked,
        @JsonProperty("credentials_non_expired")
        Boolean credentialsNonExpired,
        @JsonProperty("enabled")
        Boolean enabled,
        @JsonProperty("password")
        String password) {
    public static @NonNull AuthUserJson fromEntity(@NonNull AuthUserEntity entity) {
        return new AuthUserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getAccountNonExpired(),
                entity.getAccountNonLocked(),
                entity.getCredentialsNonExpired(),
                entity.getEnabled(),
                entity.getPassword()
        );
    }
}
