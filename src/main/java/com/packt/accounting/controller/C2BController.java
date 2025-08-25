package com.packt.accounting.controller;



import com.packt.accounting.service.MpesaC2BService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class C2BController {

    @Autowired
    private MpesaC2BService mpesaC2BService;

    @PostMapping("/c2b")
    public Map<String, String> doC2B(
            @RequestParam String amount,
            @RequestParam String msisdn,
            @RequestParam String reference
    ) throws Exception {
        return mpesaC2BService.doC2BPayment(amount, msisdn, reference);
    }
}
