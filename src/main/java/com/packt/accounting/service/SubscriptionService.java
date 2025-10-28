package com.packt.accounting.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


@Service
public class SubscriptionService {
	
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    //find user id by email or username
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
	//1 This function will activate subscription
    public Map<String, Object> activateSubscription(int userId, String type) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate;

        if ("MONTHLY".equalsIgnoreCase(type)) {
            endDate = startDate.plusMonths(1);
        } else if ("YEARLY".equalsIgnoreCase(type)) {
            endDate = startDate.plusYears(1);
        } else {
            throw new IllegalArgumentException("Invalid subscription type: " + type);
        }

        // Check if subscription already exists for user
        String checkSql = "SELECT COUNT(*) FROM subscriptions WHERE consumer_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId);

        if (count != null && count > 0) {
            // Update existing subscription
        	String updatesql2 = "select end_date from subscriptions where consumer_id  = ?";
        	LocalDateTime end_date1 = jdbcTemplate.queryForObject(updatesql2,LocalDateTime.class, userId);
        	if(type == "yearly") {
        		endDate= end_date1.plusYears(1);
        	} else {
        		endDate= end_date1.plusMonths(1);
        	} 
        	
            String updateSql = "UPDATE subscriptions SET type = ?, start_date = ?, end_date = ?, status = 'ACTIVE' WHERE consumer_id = ?";
            jdbcTemplate.update(updateSql, type, startDate, endDate, userId);
        } else {
            // Insert new subscription
            String insertSql = "INSERT INTO subscriptions (consumer_id, type, start_date, end_date, status) VALUES (?, ?, ?, ?, 'ACTIVE')";
            jdbcTemplate.update(insertSql, userId, type, startDate, endDate);
        }

        // Return details
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Subscription activated");
        response.put("activatedAt", startDate);
        response.put("expiresAt", endDate);
        response.put("status", "ACTIVE");

        return response;
    }
    
  //3 call this one to update the status of subscription before doing anything, e.g before logging the user to check whether he has active subscription
    public void updateExpiredSubscriptionsOnCheck() {
        String sql = "UPDATE subscriptions SET status = 'INACTIVE' WHERE end_date < ? AND status = 'ACTIVE'";
        jdbcTemplate.update(sql, LocalDateTime.now());
    }
//This function will be used to find the subscription status of a certain personnel 
    public Map<String, Object> getSubscriptionByUser(int userId) {
        updateExpiredSubscriptionsOnCheck();
        try {
            String sql = "SELECT status, start_date, end_date FROM subscriptions WHERE consumer_id = ?";
            return jdbcTemplate.queryForMap(sql, userId);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("status", "NONE");
            response.put("message", "No subscription found.");
            return response;
        }
    }

    

    // -----------------------
    //2 use this fuction when checking whether the user has active subscription
    public boolean hasActiveSubscription(int userId) {
        try {
            // First: deactivate expired subscriptions for this user
            String deactivateSql = "UPDATE subscriptions " +
                                   "SET status = 'INACTIVE' " +
                                   "WHERE consumer_id = ? AND status = 'ACTIVE' AND end_date <= NOW()";
            jdbcTemplate.update(deactivateSql, userId);

            // Then: check if an active subscription exists
            String sql = "SELECT COUNT(*) FROM subscriptions " +
                         "WHERE consumer_id = ? AND status = 'ACTIVE' AND end_date > NOW()";

            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);

            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Error checking subscription for userId=" + userId + ": " + e.getMessage());
            return false;
        }
    }



    // SCHEDULED EXPIRY TASK
    @Scheduled(fixedRate = 60 * 60 * 1000) // Runs every hour
    public void deactivateExpiredSubscriptions() {
        String sql = "UPDATE subscriptions SET status = 'INACTIVE' WHERE end_date <= NOW() AND status = 'ACTIVE'";
        int updatedRows = jdbcTemplate.update(sql);
        if (updatedRows > 0) {
            System.out.println("Expired subscriptions deactivated: " + updatedRows);
        }
    }

    // -----------------------
  


}
