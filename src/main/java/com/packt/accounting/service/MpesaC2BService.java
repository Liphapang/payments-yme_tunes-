package com.packt.accounting.service;

import mu.prevoir.sdk.APIContext;
import mu.prevoir.sdk.APIMethodType;
import mu.prevoir.sdk.APIRequest;
import mu.prevoir.sdk.APIResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MpesaC2BService {

    private static final String PUBLIC_KEY =
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArv9yxA69XQKBo24BaF/D+fvlqmGdYjqLQ5WtNBb5tquqGvAvG3WMFETVUSow/LizQalxj2ElMVrUmzu5mGGkxK08bWEXF7a1DEvtVJs6nppIlFJc2SnrU14AOrIrB28ogm58JjAl5BOQawOXD5dfSk7MaAA82pVHoIqEu0FxA8BOKU+RGTihRU+ptw1j4bsAJYiPbSX6i71gfPvwHPYamM0bfI4CmlsUUR3KvCG24rB6FNPcRBhM3jDuv8ae2kC33w9hEq8qNB55uw51vK7hyXoAa+U7IqP1y6nBdlN25gkxEA8yrsl1678cspeXr+3ciRyqoRgj9RD/ONbJhhxFvt1cLBh+qwK2eqISfBb06eRnNeC71oBokDm3zyCnkOtMDGl7IvnMfZfEPFCfg5QgJVk1msPpRvQxmEsrX9MQRyFVzgy2CWNIb7c+jPapyrNwoUbANlN8adU1m6yOuoX7F49x+OjiG2se0EJ6nafeKUXw/+hiJZvELUYgzKUtMAZVTNZfT8jjb58j8GVtuS+6TM2AutbejaCV84ZK58E2CRJqhmjQibEUO6KPdD7oTlEkFy52Y1uOOBXgYpqMzufNPmfdqqqSM4dU70PO8ogyKGiLAIxCetMjjm6FCMEA3Kc8K0Ig7/XtFm9By6VxTJK1Mg36TlHaZKP6VzVLXMtesJECAwEAAQ==";

    private static final String API_KEY = "KnDAZpsoO0Q3UU3p0FC7rVu4bOyz7UtP";

    /**
     * Fetch fresh sessionID
     */
    private String getSession() throws Exception {
        APIContext context = APIContext.builder()
                .apiKey(API_KEY)
                .publicKey(PUBLIC_KEY)
                .ssl(true)
                .apiMethodType(APIMethodType.GET)
                .address("openapi.m-pesa.com")
                .port(443)
                .path("/sandbox/ipg/v2/vodacomLES/getSession/")
                .build();

        context.addHeader("Origin", "*");

        APIRequest request = new APIRequest(context);
        APIResponse response = request.execute();

        System.out.println("RAW RESPONSE: " + response.getBody()); // 👈 Add this

        if (response != null && response.getBody().containsKey("output_SessionID")) {
            return response.getBody().get("output_SessionID");
        }

        throw new Exception("Failed to fetch SessionID. Reason: " + response.getBody());
    }

    /**
     * Perform C2B Payment
     */
    public Map<String, String> doC2BPayment(String amount, String msisdn, String reference) throws Exception {
        String sessionId = getSession();

        APIContext context = APIContext.builder()
                .apiKey(sessionId) // session key
                .publicKey(PUBLIC_KEY)
                .ssl(true)
                .apiMethodType(APIMethodType.POST)
                .address("openapi.m-pesa.com")
                .port(443)
                .path("/sandbox/ipg/v2/vodacomLES/c2bPayment/singleStage/")
                .build();

        context.addParameter("input_Amount", amount);
        context.addParameter("input_Country", "LES");
        context.addParameter("input_Currency", "LSL");
        context.addParameter("input_CustomerMSISDN", msisdn);
        context.addParameter("input_ServiceProviderCode", "000000");
        context.addParameter("input_ThirdPartyConversationID", "asv02e5958774f7ba228d83d0d689761");
        context.addParameter("input_TransactionReference", "monthly");
        context.addParameter("input_PurchasedItemsDesc", "Shoes");

        context.addHeader("Origin", "*");

        // Ensure session is live
        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

        APIRequest request = new APIRequest(context);
        APIResponse response = request.execute();

        return response.getBody();
    }
}
