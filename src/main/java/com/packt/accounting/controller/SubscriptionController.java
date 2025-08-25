package com.packt.accounting.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.packt.accounting.service.SubscriptionService;

@RestController
@RequestMapping("/api/users/subscription")
public class SubscriptionController {
	
	@Autowired
	SubscriptionService SubscriptionService;
	// LOOKUP userId by email or username
    //GET /api/users/lookup?identifier=john@example.com
    @GetMapping("/lookup")
    public Map<String, Object> getUserId(@RequestParam String identifier) {
        Integer userId = SubscriptionService.getUserIdByEmailOrUsername(identifier);

        Map<String, Object> response = new HashMap<>();
        response.put("identifier", identifier);

        if (userId != null) {
            response.put("userId", userId);
            response.put("message", "User found.");
        } else {
            response.put("message", "User not found.");
        }

        return response;
    }

    
  //1 this endpoint, call it during subscription buying
    @PostMapping("/buy_subscription")
    public Map<String, Object> buysubscription( @RequestParam int userId, @RequestParam String type) {

            return SubscriptionService.activateSubscription(userId, type);
     
    }
    
 //2 CHECK if user has an active subscription
    @GetMapping("/{id}/subscription/status")
    public Map<String, Object> checkSubscriptionStatus(@PathVariable int id) {
        boolean isActive = SubscriptionService.hasActiveSubscription(id);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", id);
        response.put("active", isActive);

        if (isActive) {
            response.put("message", "User has an active subscription.");
        } else {
            response.put("message", "User does not have an active subscription.");
        }

        return response;
    }
    
    //3 run it to update expired subscriptin in the entire database
    @PutMapping("/Updateexpiredsubscriptions")
    public String UpdateexpiredSubscription( ) {
    	String success = "updated expired subscriptions";
    	try {
    	    SubscriptionService.updateExpiredSubscriptionsOnCheck();
    	} catch (Exception e) {
    	    // Handle the exception here
    	    System.err.println("An error occurred while updating expired subscriptions: " + e.getMessage());
    	    e.printStackTrace(); // Optional: prints the full stack trace
    	}

            return success;
     
    }
    
    


}
