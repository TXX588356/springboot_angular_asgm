package com.example.meal_catalogue_planner.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.dto.AuthRequest;
import com.example.meal_catalogue_planner.dto.AuthResponse;
import com.example.meal_catalogue_planner.dto.RegisterRequest;
import com.example.meal_catalogue_planner.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    // Injects authentication services used by the auth endpoints.
    public AuthController(
        AuthService authService,
        AuthenticationManager authenticationManager
    ) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    // Handles account registration requests.
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        // Authentication requirement: registration stores a BCrypt password hash, never the plaintext password.
        return authService.register(request);
    }

    // Authenticates login credentials and returns a session token.
    @PostMapping("/login")
    public AuthResponse login(
        @Valid @RequestBody AuthRequest request
    ) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            // Create an explicit server-side session token so refresh does not depend on cookies.
            return authService.createSession(request.email());
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    // Returns the currently authenticated user's account data.
    @GetMapping("/me")
    public AuthResponse me(Principal principal, HttpServletRequest request) {
        String token = resolveBearerToken(request);

        if (token != null && !token.isBlank()) {
            return authService.getBySessionToken(token);
        }

        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        return authService.getByEmail(principal.getName());
    }

    // Ends the current bearer-token or HTTP session authentication state.
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        authService.deleteSession(resolveBearerToken(request));
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

    // Extracts a bearer token from the Authorization header.
    private String resolveBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring("Bearer ".length()).trim();
    }
}
