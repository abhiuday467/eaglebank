package com.eaglebank.user.repository;

import com.eaglebank.user.model.UserEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(UserEntity user) {
        String sql = """
                INSERT INTO schema_user.profiles (user_id, full_name, phone_number,
                    address_line1, address_line2, address_line3, address_town, address_county,
                    address_postcode, created_at, updated_at, is_deleted)
                VALUES (:id, :fullName, :phoneNumber,
                    :addressLine1, :addressLine2, :addressLine3, :addressTown, :addressCounty,
                    :addressPostcode, :createdAt, :updatedAt, :isDeleted)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("fullName", user.getFullName())
                .addValue("phoneNumber", user.getPhoneNumber())
                .addValue("addressLine1", user.getAddressLine1())
                .addValue("addressLine2", user.getAddressLine2())
                .addValue("addressLine3", user.getAddressLine3())
                .addValue("addressTown", user.getAddressTown())
                .addValue("addressCounty", user.getAddressCounty())
                .addValue("addressPostcode", user.getAddressPostcode())
                .addValue("createdAt", user.getCreatedAt())
                .addValue("updatedAt", user.getUpdatedAt())
                .addValue("isDeleted", user.isDeleted());

        jdbcTemplate.update(sql, params);
    }

    public Optional<UserEntity> findById(String userId) {
        String sql = """
                SELECT user_id, full_name, phone_number, address_line1, address_line2, address_line3,
                       address_town, address_county, address_postcode, created_at, updated_at, is_deleted
                FROM schema_user.profiles
                WHERE user_id = :userId AND is_deleted = FALSE
                """;
        return jdbcTemplate.query(sql, new MapSqlParameterSource("userId", userId), rs -> {
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        });
    }

    private UserEntity mapRow(ResultSet rs) throws SQLException {
        return UserEntity.builder()
                .id(rs.getString("user_id"))
                .fullName(rs.getString("full_name"))
                .phoneNumber(rs.getString("phone_number"))
                .addressLine1(rs.getString("address_line1"))
                .addressLine2(rs.getString("address_line2"))
                .addressLine3(rs.getString("address_line3"))
                .addressTown(rs.getString("address_town"))
                .addressCounty(rs.getString("address_county"))
                .addressPostcode(rs.getString("address_postcode"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .deleted(rs.getBoolean("is_deleted"))
                .build();
    }
}
