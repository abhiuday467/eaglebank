package com.eaglebank.security.service;

public interface SecurityService {
    void createCredentials(String userId, String email, String rawPassword);
}
