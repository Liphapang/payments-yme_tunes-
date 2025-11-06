package com.packt.accounting.service;

import mu.prevoir.sdk.APIContext;
import mu.prevoir.sdk.APIMethodType;
import mu.prevoir.sdk.APIRequest;
import mu.prevoir.sdk.APIResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MpesaB2CService {

    private static final String PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAietPTdEyyoV/wvxRjS5pSn3ZBQH9hnVtQC9SFLgM9IkomEX9Vu9fBg2MzWSSqkQlaYIGFGH3d69Q5NOWkRo+Y8p5a61sc9hZ+ItAiEL9KIbZzhnMwi12jUYCTff0bVTsTGSNUePQ2V42sToOIKCeBpUtwWKhhW3CSpK7S1iJhS9H22/BT/pk21Jd8btwMLUHfVD95iXbHNM8u6vFaYuHczx966T7gpa9RGGXRtiOr3ScJq1515tzOSOsHTPHLTun59nxxJiEjKoI4Lb9h6IlauvcGAQHp5q6/2XmxuqZdGzh39uLac8tMSmY3vC3fiHYC3iMyTb7eXqATIhDUOf9mOSbgZMS19iiVZvz8igDl950IMcelJwcj0qCLoufLE5y8ud5WIw47OCVkD7tcAEPmVWlCQ744SIM5afw+Jg50T1SEtu3q3GiL0UQ6KTLDyDEt5BL9HWXAIXsjFdPDpX1jtxZavVQV+Jd7FXhuPQuDbh12liTROREdzatYWRnrhzeOJ5Se9xeXLvYSj8DmAI4iFf2cVtWCzj/02uK4+iIGXlX7lHP1W+tycLS7Pe2RdtC2+oz5RSSqb5jI4+3iEY/vZjSMBVk69pCDzZy4ZE8LBgyEvSabJ/cddwWmShcRS+21XvGQ1uXYLv0FCTEHHobCfmn2y8bJBb/Hct53BaojWUCAwEAAQ==";
    private static final String API_KEY = "RZJBybDtAO5n1xb8B86w8t9HzTxGUacv";
    private static final String COUNTRY = "LES";
    private static final String CURRENCY = "LSL";
    private static final String SERVICE_PROVIDER_CODE = "778243";

    /**
     * Fetch a fresh SessionID from M-Pesa
     */
    private String getSession() throws Exception {
        System.out.println("🔐 Starting session request...");

        APIContext context = APIContext.builder()
                .apiKey(API_KEY)
                .publicKey(PUBLIC_KEY)
                .ssl(true)
                .apiMethodType(APIMethodType.GET)
                .address("openapi.m-pesa.com")
                .port(443)
                .path("/openapi/ipg/v2/vodacomLES/getSession/")
                .build();

        context.addHeader("Origin", "*");

        APIRequest request = new APIRequest(context);
        APIResponse response = null;

        try {
            response = request.execute();
        } catch (Exception e) {
            System.out.println("❌ Exception during session request: " + e.getMessage());
            throw new Exception("Session request failed due to exception", e);
        }

        if (response == null) {
            System.out.println("❌ Session request returned null response.");
            throw new Exception("Session request failed: response was null.");
        }

        System.out.println("✅ Session response received.");
        System.out.println("🔍 Raw session response: " + response.getResult());

        if (response.getBody() != null && response.getBody().containsKey("output_SessionID")) {
            String sessionId = response.getBody().get("output_SessionID");
            System.out.println("🔑 SessionID acquired: " + sessionId);
            return sessionId;
        }

        System.out.println("❌ SessionID not found in response body.");
        throw new Exception("SessionID not found in response.");
    }



    /**
     * Perform B2C Payment
     */
    public Map<String, String> doB2CPayment(String amount, String msisdn, String reference,
                                            String description, String thirdPartyConversationId) throws Exception {

        System.out.println("🚀 Initiating B2C payment...");
        System.out.println("📦 Parameters → amount: " + amount + ", msisdn: " + msisdn +
                ", reference: " + reference + ", description: " + description +
                ", thirdPartyConversationId: " + thirdPartyConversationId);

        String sessionId = getSession();

        APIContext context = APIContext.builder()
                .apiKey(sessionId)
                .publicKey(PUBLIC_KEY)
                .ssl(true)
                .apiMethodType(APIMethodType.POST)
                .address("openapi.m-pesa.com")
                .port(443)
                .path("/openapi/ipg/v2/vodacomLES/b2cPayment/")
                .build();

        context.addParameter("input_Amount", amount);
        context.addParameter("input_Country", COUNTRY);
        context.addParameter("input_Currency", CURRENCY);
        context.addParameter("input_CustomerMSISDN", msisdn);
        context.addParameter("input_ServiceProviderCode", SERVICE_PROVIDER_CODE);
        context.addParameter("input_TransactionReference", reference);
        context.addParameter("input_ThirdPartyConversationID", thirdPartyConversationId);
        context.addParameter("input_PaymentItemsDesc", description);

        context.addHeader("Origin", "*");

        System.out.println("⏳ Waiting for session to activate...");
        Thread.sleep(TimeUnit.SECONDS.toMillis(30));

        APIRequest request = new APIRequest(context);
        APIResponse response;

        try {
            response = request.execute();
            System.out.println("✅ B2C response received.");
            System.out.println("📨 Raw B2C response: " + response.getResult());
        } catch (Exception e) {
            System.out.println("❌ B2C payment failed: " + e.getMessage());
            throw new Exception("B2C payment request failed", e);
        }

        if (response != null && response.getBody() != null) {
            System.out.println("📊 Parsed B2C response:");
            for (Map.Entry<String, String> entry : response.getBody().entrySet()) {
                System.out.println("🔹 " + entry.getKey() + ": " + entry.getValue());
            }
            return response.getBody();
        }

        throw new Exception("Empty or null response body from B2C API.");
    }
}