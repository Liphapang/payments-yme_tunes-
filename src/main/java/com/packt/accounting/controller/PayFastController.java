package com.packt.accounting.controller;

import com.packt.accounting.service.PayFastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/payfast")
@CrossOrigin(origins = "*")  // Allow all origins (React Native, web, etc.)
public class PayFastController {

    @Autowired
    private PayFastService payFastService;

    /**
     * Create a PayFast payment link with callback support
     * Example: POST http://localhost:8080/api/payments/payfast/initiate?amount=10&reference=TEST123&callbackUrl=https://your-app.com/callback
     */
    @PostMapping("/initiate")
    public String initiatePayment(
            @RequestParam String amount,
            @RequestParam String reference,
            @RequestParam String callbackUrl) {
        return payFastService.initiatePayment(amount, reference, callbackUrl);
    }

    /**
     * Receive PayFast notifications (IPN)
     * Example: POST from PayFast → /api/payments/payfast/notify
     */
    @PostMapping("/notify")
    public ResponseEntity<String> handleNotification(@RequestParam Map<String, String> payload) {
        System.out.println("🔔 IPN received: " + payload);
        payFastService.handleNotification(payload);
        return ResponseEntity.ok("OK");
    }
    /**
     * Check transaction status
     * Example: GET http://localhost:8080/api/payments/payfast/status?transactionId=TEST123
     */
    @GetMapping("/status")
    public Map<String, String> checkStatus(@RequestParam("transactionId") String transactionId) {
        return payFastService.getTransactionStatus(transactionId);
    }

    /**
     * Check if IPN has been received for a transaction
     * Example: GET http://localhost:8080/api/payments/payfast/ipn-received?transactionId=TEST123
     */
    @GetMapping("/ipn-received")
    public Map<String, Boolean> hasIpnBeenReceived(@RequestParam("transactionId") String transactionId) {
        return payFastService.hasIpnBeenReceived(transactionId);
    }
}