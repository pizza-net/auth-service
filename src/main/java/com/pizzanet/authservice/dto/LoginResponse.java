package com.pizzanet.authservice.dto;

public record LoginResponse(
        String token,
        String username,
        String role,
        String message
) {}