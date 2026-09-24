package com.example.meal_catalogue_planner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.meal_catalogue_planner.dto.AuthResponse;
import com.example.meal_catalogue_planner.dto.RegisterRequest;
import com.example.meal_catalogue_planner.entity.UserAccount;
import com.example.meal_catalogue_planner.entity.UserSession;
import com.example.meal_catalogue_planner.repository.UserAccountRepository;
import com.example.meal_catalogue_planner.repository.UserSessionRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerTrimsUniqueFieldsAndStoresEncodedPassword() {
        when(userAccountRepository.existsByUsernameIgnoreCase("sam")).thenReturn(false);
        when(userAccountRepository.existsByEmailIgnoreCase("sam@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.register(new RegisterRequest(
            " sam ",
            " sam@example.com ",
            "secret123"
        ));

        assertThat(response.username()).isEqualTo("sam");
        assertThat(response.email()).isEqualTo("sam@example.com");
        assertThat(response.sessionToken()).isNull();

        ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getPasswordHash()).isEqualTo("encoded-password");
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userAccountRepository.existsByUsernameIgnoreCase("sam")).thenReturn(false);
        when(userAccountRepository.existsByEmailIgnoreCase("sam@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest(
            "sam",
            "sam@example.com",
            "secret123"
        )))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void createSessionCleansExpiredSessionsAndReturnsSavedToken() {
        UserAccount userAccount = user("sam@example.com");
        when(userAccountRepository.findByEmailIgnoreCase("sam@example.com")).thenReturn(Optional.of(userAccount));
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.createSession("sam@example.com");

        assertThat(response.email()).isEqualTo("sam@example.com");
        assertThat(response.sessionToken()).isNotBlank();

        ArgumentCaptor<LocalDateTime> cleanupCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(userSessionRepository).deleteByExpiresAtBefore(cleanupCaptor.capture());
        assertThat(cleanupCaptor.getValue()).isBeforeOrEqualTo(LocalDateTime.now());

        ArgumentCaptor<UserSession> sessionCaptor = ArgumentCaptor.forClass(UserSession.class);
        verify(userSessionRepository).save(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().getUserAccount()).isSameAs(userAccount);
        assertThat(sessionCaptor.getValue().getToken()).isEqualTo(response.sessionToken());
        assertThat(sessionCaptor.getValue().getExpiresAt()).isAfter(LocalDateTime.now().plusHours(7));
    }

    @Test
    void getUserBySessionTokenDeletesExpiredSessionAndReturnsUnauthorized() {
        UserSession expiredSession = new UserSession();
        expiredSession.setToken("old-token");
        expiredSession.setUserAccount(user("sam@example.com"));
        expiredSession.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(userSessionRepository.findByToken("old-token")).thenReturn(Optional.of(expiredSession));

        assertThatThrownBy(() -> authService.getUserBySessionToken("old-token"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));

        verify(userSessionRepository).delete(expiredSession);
    }

    private static UserAccount user(String email) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("sam");
        userAccount.setEmail(email);
        return userAccount;
    }
}
