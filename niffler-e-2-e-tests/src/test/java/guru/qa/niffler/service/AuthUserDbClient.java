package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.AuthUserJson;

import java.sql.Connection;


public class AuthUserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_SERIALIZABLE;
    private final JdbcTransactionTemplate jdbcTemplate = new JdbcTransactionTemplate(CFG.authJdbcUrl());
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();

    public AuthUserJson createAuthUser(AuthUserJson authUserJson) {
        return jdbcTemplate.execute(() -> {
                    AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUserJson);
                    return AuthUserJson.fromEntity(authUserDao.createUser(authUserEntity));
                },
                TRANSACTION_ISOLATION_LEVEL);
    }
}
