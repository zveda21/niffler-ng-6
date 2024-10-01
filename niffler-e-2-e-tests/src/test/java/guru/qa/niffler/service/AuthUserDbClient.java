package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.model.AuthUserJson;

import java.sql.Connection;

import static guru.qa.niffler.data.Databases.transaction;

public class AuthUserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_SERIALIZABLE;

    public AuthUserJson createAuthUser(AuthUserJson authUserJson) {
        return transaction(connection -> {
                    AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUserJson);
                    return AuthUserJson.fromEntity(new AuthUserDaoJdbc(connection).createUser(authUserEntity));
                },
                CFG.authJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }
}
