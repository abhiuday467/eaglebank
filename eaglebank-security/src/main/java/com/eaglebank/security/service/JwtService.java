package com.eaglebank.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

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

    String generateToken(String userId) {
        Instant now = Instant.now(clock);
        Instant exp = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(getSignInKey())
                .compact();
    }

    public String validateTokenAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid or Expired Token", ex);
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
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
