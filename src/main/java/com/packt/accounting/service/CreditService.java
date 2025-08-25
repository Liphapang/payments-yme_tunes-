package com.packt.accounting.service;

import com.packt.accounting.dao.CreditDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditService {

    @Autowired
    private CreditDao creditDao;

    public void addCredits(int userId, int amount) {
        creditDao.addCredits(userId, amount);
    }

    public boolean deductCredits(int userId, int amount) {
        return creditDao.deductCredits(userId, amount);
    }

    public Integer getCredits(int userId) {
        return creditDao.getCredits(userId);
    }
}
