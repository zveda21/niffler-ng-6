package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserDaoJdbc implements UserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity createUser(UserEntity user) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small , full_name) " +
                            "VALUES ( ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());
                ps.setString(3, user.getFirstname());
                ps.setString(4, user.getSurname());
                ps.setBytes(5, user.getPhoto());
                ps.setBytes(6, user.getPhotoSmall());
                ps.setString(7, user.getFullname());

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
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating user", e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM user where id=?"
            )) {
                ps.setObject(1, id);
                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        UserEntity userEntity = new UserEntity();

                        userEntity.setId(rs.getObject("id", UUID.class));
                        userEntity.setUsername(rs.getString("username"));
                        userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        userEntity.setFirstname(rs.getString("firstname"));
                        userEntity.setSurname(rs.getString("surname"));
                        userEntity.setPhoto(rs.getBytes("photo"));
                        userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                        userEntity.setFullname(rs.getString("full_name"));

                        return Optional.of(userEntity);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding User by ID: " + id, e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {

        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM user WHERE username = ?"
            )) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        UserEntity userEntity = new UserEntity();

                        userEntity.setId(rs.getObject("id", UUID.class));
                        userEntity.setUsername(rs.getString("username"));
                        userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        userEntity.setFirstname(rs.getString("firstname"));
                        userEntity.setSurname(rs.getString("surname"));
                        userEntity.setPhoto(rs.getBytes("photo"));
                        userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                        userEntity.setFullname(rs.getString("full_name"));

                        return Optional.of(userEntity);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding user by username: " + username, e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM user WHERE id = ?"
            )) {
                ps.setObject(1, user.getId());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while connecting to the database for deleting user with ID: " + user.getId(), e);
        }
    }
}
