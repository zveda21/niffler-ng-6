package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendingEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {

    private final JdbcTemplate jdbcTemplate;

    public SpendDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public SpendEntity create(SpendEntity spend) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, spend.getUsername());
            ps.setDate(2, spend.getSpendDate());
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM spend WHERE id = ?");
            ps.setObject(1, id);
            return ps;
        }, SpendingEntityRowMapper.instance).stream().findFirst();
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM spend WHERE username = ?");
            ps.setString(1, username);
            return ps;
        }, SpendingEntityRowMapper.instance);
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM spend WHERE id = ?");
            ps.setObject(1, spend.getId());
            return ps;
        });
    }

    @Override
    public List<SpendEntity> findAll() {
        return jdbcTemplate.query(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM spend");
            return ps;
        }, SpendingEntityRowMapper.instance);
    }
}
