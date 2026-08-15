package com.example.meal_catalogue_planner.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.dto.AuthResponse;
import com.example.meal_catalogue_planner.dto.RegisterRequest;
import com.example.meal_catalogue_planner.entity.UserAccount;
import com.example.meal_catalogue_planner.entity.UserSession;
import com.example.meal_catalogue_planner.repository.UserAccountRepository;
import com.example.meal_catalogue_planner.repository.UserSessionRepository;

@Service
public class AuthService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        UserAccountRepository userAccountRepository,
        UserSessionRepository userSessionRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userSessionRepository = userSessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim();

        if (userAccountRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already registered");
        }

        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(username);
        userAccount.setEmail(email);
        userAccount.setPasswordHash(passwordEncoder.encode(request.password()));

        return toResponse(userAccountRepository.save(userAccount), null);
    }

    public AuthResponse getByUsername(String username) {
        UserAccount userAccount = userAccountRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found"));

        return toResponse(userAccount, null);
    }

    public AuthResponse getByEmail(String email) {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found"));

        return toResponse(userAccount, null);
    }

    @Transactional
    public AuthResponse createSession(String email) {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found"));

        userSessionRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        UserSession userSession = new UserSession();
        userSession.setUserAccount(userAccount);
        userSession.setToken(UUID.randomUUID().toString());
        userSession.setExpiresAt(LocalDateTime.now().plusHours(8));

        UserSession savedSession = userSessionRepository.save(userSession);
        return toResponse(userAccount, savedSession.getToken());
    }

    @Transactional
    public UserAccount getUserBySessionToken(String token) {
        UserSession userSession = userSessionRepository.findByToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session"));

        if (userSession.getExpiresAt().isBefore(LocalDateTime.now())) {
            userSessionRepository.delete(userSession);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired");
        }

        return userSession.getUserAccount();
    }

    public AuthResponse getBySessionToken(String token) {
        return toResponse(getUserBySessionToken(token), token);
    }

    @Transactional
    public void deleteSession(String token) {
        if (token != null && !token.isBlank()) {
            userSessionRepository.deleteByToken(token);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        // Login uses email as the Spring Security principal; username remains display/profile data.
        return User.withUsername(userAccount.getEmail())
            .password(userAccount.getPasswordHash())
            .roles("USER")
            .build();
    }

    private AuthResponse toResponse(UserAccount userAccount, String sessionToken) {
        return new AuthResponse(
            userAccount.getId(),
            userAccount.getUsername(),
            userAccount.getEmail(),
            sessionToken
        );
    }
}
