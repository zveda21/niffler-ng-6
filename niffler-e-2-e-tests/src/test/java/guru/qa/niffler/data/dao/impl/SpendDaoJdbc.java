package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES ( ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, spend.getUsername());
                ps.setDate(2, spend.getSpendDate());
                ps.setString(3, spend.getCurrency().name());
                ps.setDouble(4, spend.getAmount());
                ps.setString(5, spend.getDescription());
                ps.setObject(6, spend.getCategory().getId());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                spend.setId(generatedKey);
                return spend;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM spend where id=?"
            )) {
                ps.setObject(1, id);
                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        SpendEntity spendEntity = new SpendEntity();
                        Optional<CategoryEntity> categoryEntity;
                        CategoryDaoJdbc categoryDaoJdbc = new CategoryDaoJdbc();

                        spendEntity.setId(rs.getObject("id", UUID.class));
                        spendEntity.setUsername(rs.getString("username"));
                        spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        spendEntity.setSpendDate(rs.getDate("spend_date"));
                        spendEntity.setAmount(rs.getDouble("amount"));
                        spendEntity.setDescription(rs.getString("description"));

                        UUID categoryId = UUID.fromString(rs.getString("category_id"));
                        categoryEntity = categoryDaoJdbc.findCategoryById(categoryId);
                        categoryEntity.ifPresent(spendEntity::setCategory);

                        return Optional.of(spendEntity);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding SpendEntity by ID: " + id, e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        List<SpendEntity> spendList = new ArrayList<>();

        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM spend WHERE username = ?"
            )) {
                ps.setObject(1, username);
                ps.execute();

                try (ResultSet rs = ps.getResultSet()) {
                    while (rs.next()) {
                        SpendEntity spendEntity = new SpendEntity();
                        CategoryDaoJdbc categoryDaoJdbc = new CategoryDaoJdbc();

                        spendEntity.setId(rs.getObject("id", UUID.class));
                        spendEntity.setUsername(rs.getString("username"));
                        spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        spendEntity.setSpendDate(rs.getDate("spend_date"));
                        spendEntity.setAmount(rs.getDouble("amount"));
                        spendEntity.setDescription(rs.getString("description"));

                        if (rs.getObject("category_id") != null) {
                            UUID categoryId = UUID.fromString(rs.getString("category_id"));
                            Optional<CategoryEntity> categoryEntity = categoryDaoJdbc.findCategoryById(categoryId);
                            categoryEntity.ifPresent(spendEntity::setCategory);
                        }

                        spendList.add(spendEntity);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding SpendEntities for username: " + username, e);
        }
        return spendList;
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM spend WHERE id = ?"
            )) {
                ps.setObject(1, spend.getId());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while connecting to the database for deleting spending with ID: " + spend.getId(), e);
        }
    }
}
