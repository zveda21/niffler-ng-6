package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class AuthUserDaoJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {

        String encodedPassword = pe.encode(user.getPassword());
        user.setPassword(encodedPassword);

        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity authUserEntity = new AuthUserEntity();
                    authUserEntity.setId(rs.getObject("id", UUID.class));
                    authUserEntity.setUsername(rs.getString("username"));
                    authUserEntity.setPassword(rs.getString("password")); // here password is encoded
                    authUserEntity.setEnabled(rs.getBoolean("enabled"));
                    authUserEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUserEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUserEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(authUserEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity authUserEntity = new AuthUserEntity();
                    authUserEntity.setId(rs.getObject("id", UUID.class));
                    authUserEntity.setUsername(rs.getString("username"));
                    authUserEntity.setPassword(rs.getString("password")); // here password is encoded
                    authUserEntity.setEnabled(rs.getBoolean("enabled"));
                    authUserEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    authUserEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    authUserEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(authUserEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuthUser(AuthUserEntity userEntity) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, userEntity.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error while connecting to the database for deleting authUser with ID: " + userEntity.getId(), e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        List<AuthUserEntity> userList = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\"")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuthUserEntity userEntity = new AuthUserEntity();
                    userEntity.setId(rs.getObject("id", UUID.class));
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setPassword(rs.getString("password"));
                    userEntity.setEnabled(rs.getBoolean("enabled"));
                    userEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    userEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    userEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    userList.add(userEntity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving all auth users", e);
        }
        return userList;
    }
}
