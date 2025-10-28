package com.packt.accounting.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import mu.prevoir.sdk.APIContext;
import mu.prevoir.sdk.APIMethodType;
import mu.prevoir.sdk.APIRequest;
import mu.prevoir.sdk.APIResponse;




@Service
public class MpesaC2BService {

    private static final String PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAietPTdEyyoV/wvxRjS5pSn3ZBQH9hnVtQC9SFLgM9IkomEX9Vu9fBg2MzWSSqkQlaYIGFGH3d69Q5NOWkRo+Y8p5a61sc9hZ+ItAiEL9KIbZzhnMwi12jUYCTff0bVTsTGSNUePQ2V42sToOIKCeBpUtwWKhhW3CSpK7S1iJhS9H22/BT/pk21Jd8btwMLUHfVD95iXbHNM8u6vFaYuHczx966T7gpa9RGGXRtiOr3ScJq1515tzOSOsHTPHLTun59nxxJiEjKoI4Lb9h6IlauvcGAQHp5q6/2XmxuqZdGzh39uLac8tMSmY3vC3fiHYC3iMyTb7eXqATIhDUOf9mOSbgZMS19iiVZvz8igDl950IMcelJwcj0qCLoufLE5y8ud5WIw47OCVkD7tcAEPmVWlCQ744SIM5afw+Jg50T1SEtu3q3GiL0UQ6KTLDyDEt5BL9HWXAIXsjFdPDpX1jtxZavVQV+Jd7FXhuPQuDbh12liTROREdzatYWRnrhzeOJ5Se9xeXLvYSj8DmAI4iFf2cVtWCzj/02uK4+iIGXlX7lHP1W+tycLS7Pe2RdtC2+oz5RSSqb5jI4+3iEY/vZjSMBVk69pCDzZy4ZE8LBgyEvSabJ/cddwWmShcRS+21XvGQ1uXYLv0FCTEHHobCfmn2y8bJBb/Hct53BaojWUCAwEAAQ==";
    private static final String API_KEY = "Js7TFwoEL0VFoLkbpCBGvB4XS3StxvG3";

    /**
     * Fetch fresh SessionID
     */
   
    
    private String getSession() throws Exception {
        APIContext context = APIContext.builder()
                .apiKey(API_KEY)
                .publicKey(PUBLIC_KEY)
                .ssl(true)
                .apiMethodType(APIMethodType.GET)
                .address("openapi.m-pesa.com")
                .port(443)
                // IMPORTANT: use the correct country path!
                .path("/openapi/ipg/v2/vodacomLES/getSession/")
                .build();
        System.out.println("=== prepared context ===");

        context.addHeader("Origin", "*");

        APIRequest request = new APIRequest(context);
        System.out.println("=== preparing the request ===");
        APIResponse response = request.execute();

        System.out.println("=== RAW SESSION RESPONSE ===");
        System.out.println(response.getResult());

        if (response != null && response.getBody().containsKey("output_SessionID")) {
            return response.getBody().get("output_SessionID");
        }

        throw new Exception("Failed to fetch SessionID. Response: " + response.getResult());
    }

    /**
     * Perform C2B Payment
     */
    public Map<String, String> doC2BPayment(String amount, String msisdn, String reference , String description) throws Exception {
    	System.out.println("assigning the session idddddddddddd");
        String sessionId = getSession();

        APIContext context = APIContext.builder()
                .apiKey(sessionId) // session key
                .publicKey(PUBLIC_KEY)
                .ssl(true)
                .apiMethodType(APIMethodType.POST)
                .address("openapi.m-pesa.com")
                .port(443)
                .path("/openapi/ipg/v2/vodacomLES/c2bPayment/singleStage/")
                .build();

        context.addParameter("input_Amount", amount);
        context.addParameter("input_Country", "LES");
        context.addParameter("input_Currency", "LSL");
        context.addParameter("input_CustomerMSISDN", msisdn);
        context.addParameter("input_ServiceProviderCode", "778243");
        context.addParameter("input_ThirdPartyConversationID", "asv02e5958774f7ba228d83d0d689761");
        context.addParameter("input_TransactionReference", reference);
        context.addParameter("input_PurchasedItemsDesc", description);

        context.addHeader("Origin", "*");

        // IMPORTANT: wait up to 30s for SessionID to be live
        Thread.sleep(TimeUnit.SECONDS.toMillis(30));

        APIRequest request = new APIRequest(context);
        APIResponse response = request.execute();

        System.out.println("=== RAW C2B RESPONSE ===");
        System.out.println(response.getResult());

        return response.getBody();
    }
}
