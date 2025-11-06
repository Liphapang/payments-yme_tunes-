package com.packt.accounting.service;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PayFastService {

    // Sandbox merchant details MERCHANT_ID = "10041759"; MERCHANT_KEY = "ac6t0v9kw8raz";
    private static final String MERCHANT_ID = "14991361";
    private static final String MERCHANT_KEY = "jqrnx9f3yk5hy";
    private static final String RETURN_URL = "https://www.example.com/success";
    private static final String CANCEL_URL = "https://www.example.com/cancel";
    private static final String NOTIFY_URL = "https://8649db391369.ngrok-free.app/api/payments/payfast/notify";
    private static final String PAYFAST_URL = "https://www.payfast.co.za/eng/process";	

    // In-memory stores
    private final Map<String, String> transactionStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> ipnReceivedMap = new ConcurrentHashMap<>();
    private final Map<String, String> callbackMap = new ConcurrentHashMap<>();

    /**
     * Generate the PayFast payment URL and store callback
     */
    public String initiatePayment(String amount, String reference, String callbackUrl) {
        callbackMap.put(reference, callbackUrl); // Store callback for later

        try {
            Map<String, String> data = new LinkedHashMap<>();
            data.put("merchant_id", MERCHANT_ID);
            data.put("merchant_key", MERCHANT_KEY);
            data.put("return_url", RETURN_URL);
            data.put("cancel_url", CANCEL_URL);
            data.put("notify_url", NOTIFY_URL);
            data.put("name_first", "Test");
            data.put("name_last", "User");
            data.put("email_address", "test@example.com");
            data.put("m_payment_id", reference);
            data.put("amount", amount);
            data.put("item_name", "YME Tunes Subscription");

            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                sj.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            return PAYFAST_URL + "?" + sj.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error building PayFast URL", e);
        }
    }

    /**
     * Handle PayFast notification (IPN)
     */
    public void handleNotification(Map<String, String> payload) {
        System.out.println("📩 PayFast notification received: " + payload);

        String transactionId = payload.get("m_payment_id");
        String status = payload.get("payment_status");

        if (transactionId != null) {
            ipnReceivedMap.put(transactionId, true);
            if (status != null) {
                transactionStatusMap.put(transactionId, status);
            }

            String callbackUrl = callbackMap.get(transactionId);
            if (callbackUrl != null) {
                sendCallback(callbackUrl, transactionId, status);
            }
        }

        if (!isValidSignature(payload)) {
            System.out.println("❌ Invalid signature. Possible spoofed request.");
            return;
        }

        if ("COMPLETE".equalsIgnoreCase(status)) {
            System.out.println("✅ Payment successful: " + transactionId);
        } else {
            System.out.println("⚠️ Payment not completed: " + transactionId);
        }
    }

    /**
     * Validate PayFast signature
     */
    private boolean isValidSignature(Map<String, String> payload) {
        try {
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : payload.entrySet()) {
                if (!"signature".equals(entry.getKey())) {
                    sj.add(entry.getKey() + "=" + entry.getValue());
                }
            }

            String dataString = sj.toString();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(dataString.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            String calculatedSignature = sb.toString();
            String receivedSignature = payload.get("signature");

            return calculatedSignature.equalsIgnoreCase(receivedSignature);
        } catch (Exception e) {
            System.out.println("❌ Signature verification failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send callback to frontend
     */
    private void sendCallback(String url, String transactionId, String status) {
        try {
            Map<String, String> payload = Map.of(
                "transactionId", transactionId,
                "status", status
            );

            String json = new Gson().toJson(payload);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (var os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("📡 Callback sent to " + url + " → " + responseCode);
        } catch (Exception e) {
            System.out.println("❌ Failed to send callback: " + e.getMessage());
        }
    }

    /**
     * Check transaction status
     */
    public Map<String, String> getTransactionStatus(String transactionId) {
        String status = transactionStatusMap.getOrDefault(transactionId, "UNKNOWN");
        return Map.of("transactionId", transactionId, "status", status);
    }

    /**
     * Check if IPN has been received
     */
    public Map<String, Boolean> hasIpnBeenReceived(String transactionId) {
        boolean received = ipnReceivedMap.getOrDefault(transactionId, false);
        return Map.of("ipnReceived", received);
    }
}