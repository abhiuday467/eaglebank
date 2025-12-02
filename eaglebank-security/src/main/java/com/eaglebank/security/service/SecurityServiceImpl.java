package com.eaglebank.security.service;

import com.eaglebank.security.exception.UserAlreadyExistsException;
import com.eaglebank.security.repository.AuthRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class SecurityServiceImpl implements SecurityService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    SecurityServiceImpl(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createCredentials(String userId, String email, String rawPassword) {
        if (authRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
        String hashed = passwordEncoder.encode(rawPassword);
        authRepository.save(userId, email, hashed);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findEmailByUserId(String userId) {
        return authRepository.findEmailByUserId(userId);
    }
}
