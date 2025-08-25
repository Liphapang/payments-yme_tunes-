package com.packt.accounting.dao.impl;

import com.packt.accounting.dao.EarningsDao;
import com.packt.accounting.model.Earnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EarningsDaoImpl implements EarningsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void calculateAndUpdateEarnings(double revenuePool) {
        String sql = """
            INSERT INTO earnings (content_manager_id, total_streams, total_amount, last_calculated)
            SELECT 
                s.content_manager_id,
                COUNT(st.id) AS total_streams,
                (COUNT(st.id) / (SELECT COUNT(*) FROM streams)) * 0.7 * ? AS earnings_after_cut,
                NOW()
            FROM streams st
            JOIN songs s ON st.song_id = s.id
            GROUP BY s.content_manager_id
            ON DUPLICATE KEY UPDATE 
                total_streams = VALUES(total_streams),
                total_amount = VALUES(total_amount),
                last_calculated = NOW()
        """;

        jdbcTemplate.update(sql, revenuePool);
    }

    @Override
    public List<Earnings> getAllEarnings() {
        String sql = "SELECT * FROM earnings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapEarnings(rs));
    }

    private Earnings mapEarnings(ResultSet rs) throws SQLException {
        Earnings earnings = new Earnings();
        earnings.setId(rs.getLong("id"));
        earnings.setContentManagerId(rs.getLong("content_manager_id"));
        earnings.setTotalStreams(rs.getInt("total_streams"));
        earnings.setTotalAmount(rs.getBigDecimal("total_amount"));
        earnings.setLastCalculated(rs.getTimestamp("last_calculated").toLocalDateTime());
        return earnings;
    }
}
