package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.user.UserEntity;

import java.sql.Connection;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class UserDBClient {

    private static final Config CFG = Config.getInstance();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    public UserEntity create(UserEntity userEntity) { // The parameter must be changed to UserJson
        return transaction(connection -> {
                    return new UserDaoJdbc(connection).createUser(userEntity);
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);

    }

    public Optional<UserEntity> findById(UUID id) {
        return transaction(connection -> {
                    return new UserDaoJdbc(connection).findById(id);
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);

    }

    public Optional<UserEntity> findByUsername(String username) {
        return transaction(connection -> {
                    return new UserDaoJdbc(connection).findByUsername(username);
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }

    public void delete(UserEntity user) {
        transaction(connection -> {
                    new UserDaoJdbc(connection).delete(user);
                    return null;
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }
}
