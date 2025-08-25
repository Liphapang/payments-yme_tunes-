package com.packt.accounting.controller;

import com.packt.accounting.model.User;
import com.packt.accounting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    // testapi
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/test")
    public String hello() {
        return "you are good to go";
    }


    // GET all consumers
    @GetMapping("/consumers")
    public List<User> getAllConsumers() {
        return userService.getAllConsumers();
    }

    // GET all content managers
    @GetMapping("/managers")
    public List<User> getAllContentManagers() {
        return userService.getAllContentManagers();
    }

    // GET single user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    // CREATE new user
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // UPDATE existing user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        user.setUserId(id);
        return userService.createUser(user);
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
 // LOOKUP userId by email or username
    //GET /api/users/lookup?identifier=john@example.com
    @GetMapping("/lookup")
    public Map<String, Object> getUserId(@RequestParam String identifier) {
        Integer userId = userService.getUserIdByEmailOrUsername(identifier);

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

    

    
 

}

