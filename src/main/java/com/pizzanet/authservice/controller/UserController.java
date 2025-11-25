package com.pizzanet.authservice.controller;


import com.pizzanet.authservice.dto.LoginRequest;
import com.pizzanet.authservice.dto.LoginResponse;
import com.pizzanet.authservice.model.User;
import com.pizzanet.authservice.service.JwtService;
import com.pizzanet.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autentykacja użytkownika
            UserDetails user = userService.authenticate(
                    loginRequest.username(),
                    loginRequest.password()
            );

            // Generowanie tokenu JWT
            String token = jwtService.generateToken(user);

            // Zwracanie odpowiedzi z tokenem
            return ResponseEntity.ok(new LoginResponse(
                    token,
                    user.getUsername(),
                    "Login successful"
            ));
        } catch (Exception e) {
            // Zwracanie błędu 401 w przypadku niepowodzenia
            return ResponseEntity.status(401).body(
                    new LoginResponse(
                            null,
                            null,
                            "Invalid username or password"
                    )
            );
        }
    }
        @GetMapping("/users")
        public ResponseEntity<List<User>> getAllUsers() {
            return ResponseEntity.ok(userService.getUsers());
        }
    /**
     * Endpoint do weryfikacji tokenu (opcjonalny)
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtService.extractUsername(token);
                UserDetails user = userService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, user)) {
                    return ResponseEntity.ok("Token is valid for user: " + username);
                }
            }
            return ResponseEntity.status(401).body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token verification failed");
        }
    }
}
