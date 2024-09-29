package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.*;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;

    public UserEntity create(UserEntity userEntity) { // The parameter must be changed to UserJson
        return transaction(connection -> {
                    return new UdUserDaoJdbc(connection).createUser(userEntity);
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);

    }

    public Optional<UserEntity> findById(UUID id) {
        return transaction(connection -> {
                    return new UdUserDaoJdbc(connection).findById(id);
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);

    }

    public Optional<UserEntity> findByUsername(String username) {
        return transaction(connection -> {
                    return new UdUserDaoJdbc(connection).findByUsername(username);
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }

    public void delete(UserEntity user) {
        transaction(connection -> {
                    new UdUserDaoJdbc(connection).delete(user);
                    return null;
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_ISOLATION_LEVEL);
    }


    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .createUser(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .createAuthority(authorityEntities);

        return UserJson.fromEntity(
                new UdUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
                        .createUser(
                                UserEntity.fromJson(user)
                        ),
                null
        );
    }

    public UserJson createUser(UserJson user) {
        return UserJson.fromEntity(
                xaTransaction(
                        new Databases.XaFunction<>(
                                con -> {
                                    AuthUserEntity authUser = new AuthUserEntity();
                                    authUser.setPassword(pe.encode("12345"));
                                    authUser.setPassword("12345");
                                    authUser.setEnabled(true);
                                    authUser.setAccountNonExpired(true);
                                    authUser.setAccountNonLocked(true);
                                    authUser.setCredentialsNonExpired(true);
                                    new AuthUserDaoJdbc(con).createUser(authUser);
                                    new AuthAuthorityDaoJdbc(con).createAuthority(
                                            Arrays.stream(Authority.values())
                                                    .map(a -> {
                                                                AuthorityEntity ae = new AuthorityEntity();
                                                                ae.setUserId(authUser.getId());
                                                                ae.setAuthority(a);
                                                                return ae;
                                                            }
                                                    ).toArray(AuthorityEntity[]::new));
                                    return null;
                                },
                                CFG.authJdbcUrl()
                        ),
                        new Databases.XaFunction<>(
                                con -> {
                                    UserEntity ue = new UserEntity();
                                    ue.setUsername(user.username());
                                    ue.setFullname(user.fullname());
                                    ue.setCurrency(user.currency());
                                    new UdUserDaoJdbc(con).createUser(ue);
                                    return ue;
                                },
                                CFG.userdataJdbcUrl()
                        )
                ),
                null);
    }
}
