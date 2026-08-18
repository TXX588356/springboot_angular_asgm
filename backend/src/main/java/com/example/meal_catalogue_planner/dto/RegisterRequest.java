package com.example.meal_catalogue_planner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request body used to create a new user account.
public record RegisterRequest(
    // Display username chosen by the user.
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,

    // Email address used for login and account lookup.
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    // Plain-text password that will be encoded before storage.
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    String password
) {
}
