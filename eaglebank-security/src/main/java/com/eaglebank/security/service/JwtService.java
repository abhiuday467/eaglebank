package com.eaglebank.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

@Service
class JwtService {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private final String secret;
    private final Clock clock;
    private final long ttlSeconds;

    @Autowired
    JwtService(@Value("${security.jwt.secret}") String secret,
               @Value("${security.jwt.ttl-seconds:3600}") long ttlSeconds) {
        this(secret, ttlSeconds, Clock.systemUTC());
    }

    JwtService(String secret, long ttlSeconds, Clock clock) {
        this.secret = secret;
        this.clock = clock;
        this.ttlSeconds = ttlSeconds;
    }

    public String generateToken(String userId) {
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        long issuedAt = Instant.now(clock).getEpochSecond();
        long expiresAt = issuedAt + ttlSeconds;
        String payloadJson = "{\"sub\":\"" + userId + "\",\"iat\":" + issuedAt + ",\"exp\":" + expiresAt + "}";

        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = header + "." + payload;
        String signature = base64UrlEncode(hmacSha256(signingInput));

        return signingInput + "." + signature;
    }

    private byte[] hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to sign JWT", ex);
        }
    }

    private String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }
}
