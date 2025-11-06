package com.packt.accounting.controller;

import com.packt.accounting.service.MpesaB2CService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/payments")
public class B2CController {

    @Autowired
    private MpesaB2CService mpesaB2CService;

    @PostMapping("/b2c")
    public Map<String, String> doB2CPayment(
            @RequestParam String amount,
            @RequestParam String msisdn,
            @RequestParam String reference,
            @RequestParam String description,
            @RequestParam String thirdPartyConversationId
    ) throws Exception {
        return mpesaB2CService.doB2CPayment(amount, msisdn, reference, description, thirdPartyConversationId);
    }
}