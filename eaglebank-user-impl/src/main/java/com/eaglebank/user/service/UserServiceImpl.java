package com.eaglebank.user.service;

import com.eaglebank.user.api.UserService;
import com.eaglebank.user.api.model.Address;
import com.eaglebank.user.api.model.CreateUserRequest;
import com.eaglebank.user.api.model.UserResponse;
import com.eaglebank.user.exception.UserNotFoundException;
import com.eaglebank.user.model.UserEntity;
import com.eaglebank.user.repository.UserRepository;
import com.eaglebank.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Address address = request.address();

        String userId = generateUserId();
        securityService.createCredentials(userId, request.email(), request.password());

        UserEntity entity = UserEntity.builder()
                .id(userId)
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

        return mapToResponse(entity, request.email());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String email = securityService.findEmailByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return mapToResponse(user, email);
    }

    private String generateUserId() {
        return "usr-" + UUID.randomUUID().toString().replace("-", "");
    }

    private UserResponse mapToResponse(UserEntity entity, String email) {
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
                email,
                entity.getCreatedAt().atOffset(ZoneOffset.UTC),
                entity.getUpdatedAt().atOffset(ZoneOffset.UTC)
        );
    }
}
