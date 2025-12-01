package com.eaglebank.user.api.model;

import java.time.OffsetDateTime;

public record UserResponse(
        String id,
        String name,
        Address address,
        String phoneNumber,
        String email,
        OffsetDateTime createdTimestamp,
        OffsetDateTime updatedTimestamp
) {
}
