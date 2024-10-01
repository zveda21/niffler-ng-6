package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.AuthUserJson;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity {

    private UUID id;
    private String username;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private String password;

    public static AuthUserEntity fromJson(AuthUserJson json) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setId(json.id());
        authUserEntity.setUsername(json.username());
        authUserEntity.setAccountNonLocked(json.accountNonLocked());
        authUserEntity.setAccountNonExpired(json.accountNonExpired());
        authUserEntity.setCredentialsNonExpired(json.credentialsNonExpired());
        authUserEntity.setEnabled(json.enabled());
        authUserEntity.setPassword(json.password());
        return authUserEntity;
    }
}
