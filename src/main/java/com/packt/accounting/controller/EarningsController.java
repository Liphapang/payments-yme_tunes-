package com.packt.accounting.controller;

import com.packt.accounting.model.Earnings;
import com.packt.accounting.service.EarningsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/earnings")
public class EarningsController {

    @Autowired
    private EarningsService earningsService;

    // Distribute revenue pool among content managers
    @PostMapping("/distribute")
    public Map<String, Object> distributeEarnings(@RequestParam double revenuePool) {
        earningsService.distributeEarnings(revenuePool);
        return Map.of(
                "status", "success",
                "message", "Earnings distributed successfully",
                "revenuePool", revenuePool
        );
    }

    // Get all earnings
    @GetMapping
    public List<Earnings> getAllEarnings() {
        return earningsService.fetchAllEarnings();
    }
}
