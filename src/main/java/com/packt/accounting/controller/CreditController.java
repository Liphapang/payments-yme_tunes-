package com.packt.accounting.controller;

import com.packt.accounting.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*") // allow all origins for testing
@RequestMapping("/api/credits")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @PostMapping("/add")
    public String addCredits(@RequestParam int userId, @RequestParam int amount) {
        creditService.addCredits(userId, amount);
        return "Credits added successfully!";
    }

    @PostMapping("/deduct")
    public String deductCredits(@RequestParam int userId, @RequestParam int amount) {
        boolean success = creditService.deductCredits(userId, amount);
        return success ? "Credits deducted!" : "Not enough credits!";
    }

    @GetMapping("/balance")
    public Integer getCredits(@RequestParam int userId) {
        return creditService.getCredits(userId);
    }
}
