package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

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
    public static AuthUserJson fromEntity(AuthUserEntity entity) {
        return new AuthUserJson(
                entity.getId(),
                entity.getUsername(),
                entity.isAccountNonExpired(),
                entity.isAccountNonLocked(),
                entity.isCredentialsNonExpired(),
                entity.isEnabled(),
                entity.getPassword()
        );
    }
}
