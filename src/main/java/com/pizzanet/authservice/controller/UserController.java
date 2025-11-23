package com.pizzanet.authservice.controller;


import com.pizzanet.authservice.dto.LoginRequest;
import com.pizzanet.authservice.dto.LoginResponse;
import com.pizzanet.authservice.model.User;
import com.pizzanet.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try{
            userService.authenticate(loginRequest.username(), loginRequest.password());
            return ResponseEntity.ok(new LoginResponse("Login successful"));
        }catch(Exception e){
            return ResponseEntity.status(401).body(new LoginResponse("Invalid username or password"));
        }
    }
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }
}
