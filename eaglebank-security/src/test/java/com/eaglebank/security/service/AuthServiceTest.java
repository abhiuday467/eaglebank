package com.eaglebank.security.service;

import com.eaglebank.security.repository.AuthRepository;
import com.eaglebank.security.repository.AuthRepository.Credentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // no-op; @InjectMocks wires fields
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        String email = "user@example.com";
        String password = "secret";
        Credentials credentials = new Credentials("usr-123", "hashed");

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(credentials));
        when(passwordEncoder.matches(password, "hashed")).thenReturn(true);
        when(jwtService.generateToken("usr-123")).thenReturn("token-123");

        AuthService.TokenResponse response = authService.login(email, password);

        assertEquals("token-123", response.token());
        verify(authRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, "hashed");
        verify(jwtService).generateToken("usr-123");
    }

    @Test
    void login_ShouldThrow_WhenEmailNotFound() {
        when(authRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login("missing@example.com", "secret"));
    }

    @Test
    void login_ShouldThrow_WhenPasswordMismatch() {
        Credentials credentials = new Credentials("usr-123", "hashed");
        when(authRepository.findByEmail("user@example.com")).thenReturn(Optional.of(credentials));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.login("user@example.com", "wrong"));
    }
}
