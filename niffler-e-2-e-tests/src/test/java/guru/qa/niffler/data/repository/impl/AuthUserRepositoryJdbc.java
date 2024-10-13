package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.extractor.AuthUserResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)")) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getPassword());
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);

            for (AuthorityEntity a : user.getAuthorities()) {
                authorityPs.setObject(1, generatedKey);
                authorityPs.setString(2, a.getAuthority().name());
                authorityPs.addBatch();
                authorityPs.clearParameters();
            }
            authorityPs.executeBatch();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        String updateUserSql = "UPDATE \"user\" SET password = ?, enabled = ?, " +
                "account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? " +
                "WHERE id = ?";
        String clearAuthoritySql = "DELETE FROM \"authority\" WHERE user_id = ?";
        String insertAuthoritySql = "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)";
        try (PreparedStatement updateUserPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(updateUserSql);
             PreparedStatement clearAuthorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(clearAuthoritySql);
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(insertAuthoritySql)) {
            clearAuthorityPs.setObject(1, user.getId());
            clearAuthorityPs.executeUpdate();
            for (AuthorityEntity authority : user.getAuthorities()) {
                authorityPs.setObject(1, user.getId());
                authorityPs.setString(2, authority.getAuthority().name());
                authorityPs.addBatch();
            }
            authorityPs.executeBatch();
            updateUserPs.setString(1, user.getPassword());
            updateUserPs.setBoolean(2, user.getEnabled());
            updateUserPs.setBoolean(3, user.getAccountNonExpired());
            updateUserPs.setBoolean(4, user.getAccountNonLocked());
            updateUserPs.setBoolean(5, user.getCredentialsNonExpired());
            updateUserPs.setObject(6, user.getId());
            updateUserPs.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT a.id AS authority_id, a.authority,u.id AS id, u.username,u.password,u.enabled, " +
                        "u.account_non_expired,u.account_non_locked,u.credentials_non_expired FROM \"user\" u " +
                        "JOIN authority a ON u.id = a.user_id WHERE u.id = ?")) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                Map<UUID, AuthUserEntity> userMap = new AuthUserResultSetExtractor().extractData(rs);
                return Optional.ofNullable(userMap.get(id));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        String sql = "SELECT a.id as authority_id, a.authority, u.id as user_id, u.username, u.password, " +
                "u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                "FROM \"user\" u " +
                "JOIN authority a ON u.id = a.user_id " +
                "WHERE u.username = ?";
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                Map<UUID, AuthUserEntity> userMap = new AuthUserResultSetExtractor().extractData(rs);
                return userMap.values().stream().findFirst();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(AuthUserEntity user) {
        String deleteAuthoritySql = "DELETE FROM \"authority\" WHERE user_id = ?";
        String deleteUserSql = "DELETE FROM \"user\" WHERE id = ?";
        try (PreparedStatement deleteAuthorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(deleteAuthoritySql);
             PreparedStatement deleteUserPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(deleteUserSql)) {
            deleteAuthorityPs.setObject(1, user.getId());
            deleteAuthorityPs.executeUpdate();
            deleteUserPs.setObject(1, user.getId());
            deleteUserPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove user: " + user.getUsername(), e);
        }
    }
}