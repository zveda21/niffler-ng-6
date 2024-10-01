package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CategoryEntity create(CategoryEntity category) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO category (username, name, archived) " +
                            "VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM category WHERE id = ?");
            ps.setObject(1, id);
            return ps;
        }, CategoryEntityRowMapper.instance).stream().findFirst();
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM category WHERE username = ? AND name = ?");
            ps.setString(1, username);
            ps.setString(2, categoryName);
            return ps;
        }, CategoryEntityRowMapper.instance).stream().findFirst();
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM category WHERE username = ?");
            ps.setString(1, username);
            return ps;
        }, CategoryEntityRowMapper.instance);
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM category WHERE id = ?");
            ps.setObject(1, category.getId());
            return ps;
        });
    }

    @Override
    public List<CategoryEntity> findAll() {
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM category");
            return ps;
        }, CategoryEntityRowMapper.instance);
    }
}
