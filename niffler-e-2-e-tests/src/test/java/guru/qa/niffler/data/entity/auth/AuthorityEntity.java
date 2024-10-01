package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.AuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class AuthorityEntity {

    private UUID id;
    private UUID userId;
    private Authority authority;

    public static AuthorityEntity fromJson(AuthorityJson json) {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setId(json.id());
        authorityEntity.setUserId(json.userId());
        authorityEntity.setAuthority(json.authority());
        return authorityEntity;
    }
}
