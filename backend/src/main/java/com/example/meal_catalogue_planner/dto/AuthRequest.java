package com.example.meal_catalogue_planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

// Request body used when a user logs in with email and password.
public record AuthRequest(
    // Email address used as the login principal.
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    // Plain-text password submitted for authentication.
    @NotBlank(message = "Password is required")
    String password
) {
}
