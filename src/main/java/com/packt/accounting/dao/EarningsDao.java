package com.packt.accounting.dao;

import com.packt.accounting.model.Earnings;
import java.util.List;

public interface EarningsDao {
    void calculateAndUpdateEarnings(double revenuePool);
    List<Earnings> getAllEarnings();
}
