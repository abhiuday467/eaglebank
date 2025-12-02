package com.eaglebank.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class UserEntity {

    private final String id;
    private final String fullName;
    private final String phoneNumber;
    private final String addressLine1;
    private final String addressLine2;
    private final String addressLine3;
    private final String addressTown;
    private final String addressCounty;
    private final String addressPostcode;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean deleted;
}
