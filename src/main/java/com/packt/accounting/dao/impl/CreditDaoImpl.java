package com.packt.accounting.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.packt.accounting.dao.CreditDao;

@Repository
public class CreditDaoImpl implements CreditDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addCredits(int userId, int amount) {
        String sql = "UPDATE users SET credits_balance = credits_balance + ? WHERE id = ?";
        jdbcTemplate.update(sql, amount, userId);
    }

    @Override
    public boolean deductCredits(int userId, int amount) {
        Integer balance = getCredits(userId);
        if (balance != null && balance >= amount) {
            String sql = "UPDATE users SET credits_balance = credits_balance - ? WHERE id = ?";
            jdbcTemplate.update(sql, amount, userId);
            return true;
        }
        return false;
    }

    @Override
    public Integer getCredits(int userId) {
        String sql = "SELECT credits_balance FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }
}
