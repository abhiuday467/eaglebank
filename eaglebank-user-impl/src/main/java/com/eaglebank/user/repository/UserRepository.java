package com.eaglebank.user.repository;

import com.eaglebank.user.model.UserEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
