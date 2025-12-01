package com.eaglebank.user.service;

import com.eaglebank.user.api.UserService;
import com.eaglebank.user.api.model.Address;
import com.eaglebank.user.api.model.CreateUserRequest;
import com.eaglebank.user.api.model.UserResponse;
import com.eaglebank.user.model.UserEntity;
import com.eaglebank.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Address address = request.address();

        UserEntity entity = UserEntity.builder()
                .id(generateUserId())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.name())
                .phoneNumber(request.phoneNumber())
                .addressLine1(address.line1())
                .addressLine2(address.line2())
                .addressLine3(address.line3())
                .addressTown(address.town())
                .addressCounty(address.county())
                .addressPostcode(address.postcode())
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
                .build();

        userRepository.save(entity);

        return new UserResponse(
                entity.getId(),
                entity.getFullName(),
                new Address(
                        entity.getAddressLine1(),
                        entity.getAddressLine2(),
                        entity.getAddressLine3(),
                        entity.getAddressTown(),
                        entity.getAddressCounty(),
                        entity.getAddressPostcode()
                ),
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getCreatedAt().atOffset(ZoneOffset.UTC),
                entity.getUpdatedAt().atOffset(ZoneOffset.UTC)
        );
    }

    private String generateUserId() {
        return "usr-" + UUID.randomUUID().toString().replace("-", "");
    }
}
