package com.example.meal_catalogue_planner.dto;

public record AuthResponse(
    Long id,
    String username,
    String email,
    String sessionToken
) {
}
