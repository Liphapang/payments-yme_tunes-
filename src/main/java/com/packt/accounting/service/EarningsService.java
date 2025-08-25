package com.packt.accounting.service;

import com.packt.accounting.dao.EarningsDao;
import com.packt.accounting.model.Earnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EarningsService {

    @Autowired
    private EarningsDao earningsDao;

    public void distributeEarnings(double revenuePool) {
        earningsDao.calculateAndUpdateEarnings(revenuePool);
    }

    public List<Earnings> fetchAllEarnings() {
        return earningsDao.getAllEarnings();
    }
}
