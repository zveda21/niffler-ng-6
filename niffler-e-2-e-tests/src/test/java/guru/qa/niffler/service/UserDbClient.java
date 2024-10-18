package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryImpl;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import lombok.NonNull;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private static final int TRANSACTION_ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
    //Spring Jdbc dao's
    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();
    // Jdbc dao's
    private final AuthUserDao authUserJdbcDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityJdbcDao = new AuthAuthorityDaoJdbc();
    private final UdUserDao udUserJdbcDao = new UdUserDaoJdbc();

    //Auth user Repository
    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();

    // userdata user Repository
    private final UserdataUserRepository udUserRepository = new UserdataUserRepositoryImpl();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(DataSources.dataSource(CFG.authJdbcUrl())),
                    new JdbcTransactionManager(DataSources.dataSource(CFG.userdataJdbcUrl()))
            )
    );

    private final JdbcTransactionTemplate jdbcTemplate = new JdbcTransactionTemplate(CFG.userdataJdbcUrl());

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public @NonNull UserEntity create(@NonNull UserJson userJson) {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> udUserDao.createUser(UserEntity.fromJson(userJson)), TRANSACTION_ISOLATION_LEVEL));
    }

    public @NonNull Optional<UserEntity> findById(@NonNull UUID id) {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> udUserDao.findById(id),
                TRANSACTION_ISOLATION_LEVEL));

    }

    public @NonNull Optional<UserEntity> findByUsername(@NonNull String username) {
        return Objects.requireNonNull(jdbcTemplate.execute(() -> udUserDao.findByUsername(username),
                TRANSACTION_ISOLATION_LEVEL));
    }

    public void delete(@NonNull UserEntity user) {
        jdbcTemplate.execute(() -> {
                    udUserDao.delete(user);
                    return null;
                },
                TRANSACTION_ISOLATION_LEVEL);
    }

    // spring jdbc xaTransaction
    public @NonNull UserJson createUser(@NonNull UserJson user) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDao.createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityDao.createAuthority(authorityEntities);
                    return UserJson.fromEntity(
                            udUserDao.createUser(UserEntity.fromJson(user)),
                            null
                    );
                }
        ));
    }


    public @NonNull UserJson createUserWithRepo(@NonNull UserJson user) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);
                    authUser.setAuthorities(
                            Arrays.stream(Authority.values()).map(
                                    e -> {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        ae.setUser(authUser);
                                        ae.setAuthority(e);
                                        return ae;
                                    }
                            ).toList()
                    );
                    authUserRepository.create(authUser);
                    return UserJson.fromEntity(
                            udUserDao.createUser(UserEntity.fromJson(user)),
                            null
                    );
                }
        ));
    }

    public @NonNull UserJson createUserSpringJdbcTransaction(@NonNull UserJson user) {
        return Objects.requireNonNull(txTemplate.execute(status -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDao.createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityDao.createAuthority(authorityEntities);
                    return UserJson.fromEntity(
                            udUserDao.createUser(UserEntity.fromJson(user)),
                            null
                    );
                }
        ));
    }

    public @NonNull UserJson createUserWithoutSpringJdbcTransaction(@NonNull UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDao.createUser(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(createdAuthUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        authAuthorityDao.createAuthority(authorityEntities);
        return UserJson.fromEntity(
                udUserDao.createUser(UserEntity.fromJson(user)),
                null
        );
    }

    public @NonNull UserJson createUserJdbcTransaction(@NonNull UserJson user) {
        return Objects.requireNonNull(txTemplate.execute(status -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserJdbcDao.createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityJdbcDao.createAuthority(authorityEntities);
                    return UserJson.fromEntity(
                            udUserJdbcDao.createUser(UserEntity.fromJson(user)),
                            null
                    );
                }
        ));
    }

    public @NonNull UserJson createUserWithoutJdbcTransaction(@NonNull UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserJdbcDao.createUser(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(createdAuthUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        authAuthorityJdbcDao.createAuthority(authorityEntities);
        return UserJson.fromEntity(
                udUserJdbcDao.createUser(UserEntity.fromJson(user)),
                null
        );
    }

    public @NonNull UserJson createUDUserWithRepo(@NonNull UserJson user) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> UserJson.fromEntity(
                        udUserRepository.create(UserEntity.fromJson(user)),
                        null
                )
        ));
    }

    public void addIncomeInvitation(@NonNull UserJson requester, @NonNull UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            udUserRepository.addIncomeInvitation(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    public void addOutcomeInvitation(@NonNull UserJson requester, @NonNull UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            udUserRepository.addOutcomeInvitation(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }

    public void addFriend(@NonNull UserJson requester, @NonNull UserJson addressee) {
        xaTransactionTemplate.execute(() -> {
            udUserRepository.addFriend(UserEntity.fromJson(requester), UserEntity.fromJson(addressee));
            return null;
        });
    }
}