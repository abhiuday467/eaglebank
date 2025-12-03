package com.eaglebank.user.controller;

import com.eaglebank.user.api.UserService;
import com.eaglebank.user.api.model.CreateUserRequest;
import com.eaglebank.user.api.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId, Authentication authentication) {
        String requesterId = authentication != null ? authentication.getName() : null;
        UserResponse response = userService.getUserById(userId);
        if (requesterId == null || !requesterId.equals(userId)) {
            throw new AccessDeniedException("Forbidden: cannot access other user");
        }
        return ResponseEntity.ok(response);
    }
}
