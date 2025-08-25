package com.packt.accounting.service;

import com.packt.accounting.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ========== BASIC USER OPERATIONS ==========
    public List<User> getAllConsumers() {
        String sql = "SELECT * FROM users WHERE role = 'CONSUMER'";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    public List<User> getAllContentManagers() {
        String sql = "SELECT * FROM users WHERE role = 'CONTENT_MANAGER'";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
    }

    public User createUser(User user) {
        String sql = "INSERT INTO users (username, email, password, role, credits_balance) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getRole(), user.getCreditsBalance());
        return user;
    }

    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // ========== NEW FEATURES =========1
    public Integer getUserIdByEmailOrUsername(String identifier) {
        String sql = "SELECT id FROM users WHERE email = ? OR username = ?";
        try {
            return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getInt("id"),
                identifier, identifier
            );
        } catch (Exception e) {
            return null; // not found
        }
    }


    

    // 3️⃣ Update content manager earnings based on streams
    public void updateManagerEarnings(int managerId, int streams) {
        double earningPerStream = 0.05; // 5 cents per stream example
        double earningsToAdd = streams * earningPerStream;

        jdbcTemplate.update(
                "UPDATE users SET credits_balance = credits_balance + ? WHERE user_id = ? AND role = 'MANAGER'",
                (int) earningsToAdd,
                managerId
        );

        jdbcTemplate.update(
                "INSERT INTO earnings_log (manager_id, streams, earnings, log_date) VALUES (?, ?, ?, NOW())",
                managerId,
                streams,
                earningsToAdd
        );
    }
}
