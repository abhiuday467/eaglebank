package com.eaglebank.security.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateToken_ShouldEmbedUserIdAndIat() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtService jwtService = new JwtService("test-secret-32-bytes-minimum-key!!", 3600, fixedClock);

        String token = jwtService.generateToken("usr-123");

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        assertTrue(payloadJson.contains("\"sub\":\"usr-123\""));
        assertTrue(payloadJson.contains("\"iat\":1704067200"));
        assertTrue(payloadJson.contains("\"exp\":1704070800"));
    }

    @Test
    void validateTokenAndGetUserId_ShouldReturnSubject_WhenTokenIsValid() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtService jwtService = new JwtService("test-secret-32-bytes-minimum-key!!", 60L * 60 * 24 * 365 * 50, fixedClock);

        String token = jwtService.generateToken("usr-456");

        String subject = jwtService.validateTokenAndGetUserId(token);
        assertEquals("usr-456", subject);
    }

    @Test
    void validateTokenAndGetUserId_ShouldThrowBadCredentials_WhenSignatureInvalid() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        JwtService signingService = new JwtService("signing-secret-32-bytes-minimum-key!", 60L * 60 * 24 * 365 * 50, fixedClock);
        JwtService verifyingService = new JwtService("different-secret-32-bytes-key!!", 60L * 60 * 24 * 365 * 50, fixedClock);
        String token = signingService.generateToken("usr-789");

        assertThrows(BadCredentialsException.class, () -> verifyingService.validateTokenAndGetUserId(token));
    }
}
