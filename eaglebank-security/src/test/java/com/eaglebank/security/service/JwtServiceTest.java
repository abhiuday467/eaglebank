package com.eaglebank.security.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateToken_ShouldEmbedUserIdAndIat() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtService jwtService = new JwtService("test-secret", 3600, fixedClock);

        String token = jwtService.generateToken("usr-123");

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        assertTrue(payloadJson.contains("\"sub\":\"usr-123\""));
        assertTrue(payloadJson.contains("\"iat\":1704067200"));
        assertTrue(payloadJson.contains("\"exp\":1704070800"));
    }
}
