package com.packt.accounting.model;
import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String email;
    private String password_hash;
    private String role; // "consumer", "manager", "admin"
    private int credits_balance;
    LocalDateTime created_at = LocalDateTime.now();

    // Getters
    public int getUserId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password_hash;
    }
    public String getRole() {
        return role;
    }
    public int getCreditsBalance() {
        return credits_balance;
    }

    // Setters
    public void setUserId(int userId) {
        this.id = userId;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password_hash = password;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setCreditsBalance(int creditsBalance) {
        this.credits_balance = creditsBalance;
    }
}
