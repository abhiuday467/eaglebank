package com.eaglebank.security.service;

import com.eaglebank.security.repository.AuthRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenResponse login(String email, String password) {
        var credentials = authRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid"));

        if (!passwordEncoder.matches(password, credentials.passwordHash())) {
            throw new BadCredentialsException("Invalid");
        }

        String token = jwtService.generateToken(credentials.userId());
        return new TokenResponse(token);
    }

    public record TokenResponse(String token) {
    }
}
