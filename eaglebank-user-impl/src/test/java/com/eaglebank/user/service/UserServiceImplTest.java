package com.eaglebank.user.service;

import com.eaglebank.user.api.model.Address;
import com.eaglebank.user.api.model.CreateUserRequest;
import com.eaglebank.user.api.model.UserResponse;
import com.eaglebank.user.exception.UserNotFoundException;
import com.eaglebank.user.model.UserEntity;
import com.eaglebank.user.repository.UserRepository;
import com.eaglebank.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    private final ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, securityService);
    }

    @Test
    void shouldCreateUserAndPersist_whenRequestValid() {
        CreateUserRequest request = new CreateUserRequest(
                "Test User",
                new Address("Line 1", "Line 2", "Line 3", "Town", "County", "POST"),
                "+1234567890",
                "test@example.com",
                "password123"
        );

        UserResponse response = userService.createUser(request);

        verify(userRepository).save(userCaptor.capture());
        verify(securityService).createCredentials(response.id(), "test@example.com", "password123");

        UserEntity saved = userCaptor.getValue();
        assertNotNull(saved.getId());
        assertTrue(saved.getId().startsWith("usr-"));
        assertEquals("Test User", saved.getFullName());
        assertEquals("+1234567890", saved.getPhoneNumber());
        assertEquals("Line 1", saved.getAddressLine1());
        assertEquals("Line 2", saved.getAddressLine2());
        assertEquals("Line 3", saved.getAddressLine3());
        assertEquals("Town", saved.getAddressTown());
        assertEquals("County", saved.getAddressCounty());
        assertEquals("POST", saved.getAddressPostcode());
        assertNotNull(saved.getCreatedAt());

        assertEquals(saved.getId(), response.id());
        assertEquals("Test User", response.name());
        assertEquals("test@example.com", response.email());
        assertEquals("+1234567890", response.phoneNumber());
        assertNotNull(response.createdTimestamp());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenFound() {
        String userId = "usr-123";
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        UserEntity entity = UserEntity.builder()
                .id(userId)
                .fullName("Jane Doe")
                .phoneNumber("+123")
                .addressLine1("A1")
                .addressLine2("A2")
                .addressLine3(null)
                .addressTown("Town")
                .addressCounty("County")
                .addressPostcode("POST")
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(entity));
        when(securityService.findEmailByUserId(userId)).thenReturn(Optional.of("jane@example.com"));

        UserResponse response = userService.getUserById(userId);

        assertEquals(userId, response.id());
        assertEquals("Jane Doe", response.name());
        assertEquals("jane@example.com", response.email());
        assertEquals("+123", response.phoneNumber());
        assertEquals("A1", response.address().line1());
        assertEquals("Town", response.address().town());
    }

    @Test
    void getUserById_ShouldThrow_WhenUserMissing() {
        when(userRepository.findById("usr-missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("usr-missing"));
    }

    @Test
    void getUserById_ShouldThrow_WhenEmailMissing() {
        String userId = "usr-123";
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        UserEntity entity = UserEntity.builder()
                .id(userId)
                .fullName("Jane Doe")
                .phoneNumber("+123")
                .addressLine1("A1")
                .addressLine2("A2")
                .addressLine3(null)
                .addressTown("Town")
                .addressCounty("County")
                .addressPostcode("POST")
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(entity));
        when(securityService.findEmailByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }
}
