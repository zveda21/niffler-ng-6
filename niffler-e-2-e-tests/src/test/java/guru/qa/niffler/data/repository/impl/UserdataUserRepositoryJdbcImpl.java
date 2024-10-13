package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class UserdataUserRepositoryJdbcImpl implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {

        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getFullname());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getObject(1, UUID.class));
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity user = new UserEntity();
                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    user.setFirstname(rs.getString("firstname"));
                    user.setSurname(rs.getString("surname"));
                    user.setFullname(rs.getString("full_name"));
                    user.setPhoto(rs.getBytes("photo"));
                    user.setPhotoSmall(rs.getBytes("photo_small"));
                    return Optional.of(user);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?")) {
            ps.setString(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity user = new UserEntity();
                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    user.setFirstname(rs.getString("firstname"));
                    user.setSurname(rs.getString("surname"));
                    user.setFullname(rs.getString("full_name"));
                    user.setPhoto(rs.getBytes("photo"));
                    user.setPhotoSmall(rs.getBytes("photo_small"));
                    return Optional.of(user);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity update(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET currency = ?, firstname = ?, surname = ?, photo = ?, " +
                        "photo_small = ?, full_name = ? WHERE id = ?")) {
            ps.setString(1, user.getCurrency().name());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getSurname());
            ps.setBytes(4, user.getPhoto());
            ps.setBytes(5, user.getPhotoSmall());
            ps.setString(6, user.getFullname());
            ps.setObject(7, user.getId());
            ps.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)")) {
            ps.setObject(1, addressee.getId());
            ps.setObject(2, requester.getId());
            ps.setString(3, FriendshipStatus.PENDING.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)")) {

            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, FriendshipStatus.ACCEPTED.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.addBatch();

            ps.setObject(1, addressee.getId());
            ps.setObject(2, requester.getId());
            ps.setString(3, FriendshipStatus.ACCEPTED.name());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.addBatch();
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
