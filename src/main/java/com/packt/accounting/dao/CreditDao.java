package com.packt.accounting.dao;

public interface CreditDao {
    void addCredits(int userId, int amount);
    boolean deductCredits(int userId, int amount);
    Integer getCredits(int userId);
}
