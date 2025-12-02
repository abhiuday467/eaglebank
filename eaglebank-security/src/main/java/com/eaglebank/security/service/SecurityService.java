package com.eaglebank.security.service;

import java.util.Optional;

public interface SecurityService {
    void createCredentials(String userId, String email, String rawPassword);

    Optional<String> findEmailByUserId(String userId);
}
