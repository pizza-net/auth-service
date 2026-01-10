package com.pizzanet.authservice.dto;

public record LoginResponse(
        String token,
        Long userId,
        String username,
        String role,
        String message
) {}