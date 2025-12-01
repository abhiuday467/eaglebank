package com.eaglebank.user.service;

import com.eaglebank.user.api.UserService;
import com.eaglebank.user.api.model.Address;
import com.eaglebank.user.api.model.CreateUserRequest;
import com.eaglebank.user.api.model.UserResponse;
import com.eaglebank.user.model.UserEntity;
import com.eaglebank.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Address address = request.address();

        UserEntity entity = new UserEntity(
                generateUserId(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name(),
                request.phoneNumber(),
                address.line1(),
                address.line2(),
                address.line3(),
                address.town(),
                address.county(),
                address.postcode(),
                now,
                now,
                false
        );

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
