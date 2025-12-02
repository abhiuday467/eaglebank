package com.eaglebank.security.controller;

import com.eaglebank.security.repository.AuthRepository;
import com.eaglebank.security.service.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        var credentials = authRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid"));

        if (!passwordEncoder.matches(req.password(), credentials.passwordHash())) {
            throw new BadCredentialsException("Invalid");
        }

        String token = jwtService.generateToken(credentials.userId());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record TokenResponse(String token) {
    }
}
