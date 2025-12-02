package com.eaglebank.security.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AuthRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM schema_auth.credentials WHERE email = :email LIMIT 1";
        Integer found = jdbcTemplate.query(sql, new MapSqlParameterSource("email", email), rs -> rs.next() ? 1 : null);
        return found != null;
    }

    public void save(String userId, String email, String passwordHash) {
        String sql = """
                INSERT INTO schema_auth.credentials (user_id, email, password_hash)
                VALUES (:userId, :email, :passwordHash)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("email", email)
                .addValue("passwordHash", passwordHash);
        jdbcTemplate.update(sql, params);
    }

    public Optional<String> findPasswordHashByEmail(String email) {
        String sql = "SELECT password_hash FROM schema_auth.credentials WHERE email = :email";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("email", email), rs -> {
            if (rs.next()) {
                return Optional.ofNullable(rs.getString("password_hash"));
            }
            return Optional.empty();
        });
    }

    public Optional<Credentials> findByEmail(String email) {
        String sql = """
                SELECT user_id, password_hash
                FROM schema_auth.credentials
                WHERE email = :email AND is_deleted = FALSE
                """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("email", email), rs -> {
            if (rs.next()) {
                return Optional.of(new Credentials(
                        rs.getString("user_id"),
                        rs.getString("password_hash")
                ));
            }
            return Optional.empty();
        });
    }

    public Optional<String> findEmailByUserId(String userId) {
        String sql = """
                SELECT email
                FROM schema_auth.credentials
                WHERE user_id = :userId AND is_deleted = FALSE
                """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("userId", userId), rs -> {
            if (rs.next()) {
                return Optional.ofNullable(rs.getString("email"));
            }
            return Optional.empty();
        });
    }

    public record Credentials(String userId, String passwordHash) {
    }
}
