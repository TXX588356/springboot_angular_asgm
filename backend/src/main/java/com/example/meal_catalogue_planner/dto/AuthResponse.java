package com.example.meal_catalogue_planner.dto;

// Response body returned after account lookup, registration, or session validation.
public record AuthResponse(
    // Database ID of the user account.
    Long id,

    // Display username for the account.
    String username,

    // Email address associated with the account.
    String email,

    // Active session token, or null when the response does not create or validate a session.
    String sessionToken
) {
}
