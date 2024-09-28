package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.model.CurrencyValues;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements Serializable {

    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String fullname;
    private String firstname;
    private String surname;
    private byte[] photo;
    private byte[] photoSmall;
}
