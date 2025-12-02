package com.eaglebank.user.controller;

import com.eaglebank.user.api.UserService;
import com.eaglebank.user.api.model.Address;
import com.eaglebank.user.api.model.CreateUserRequest;
import com.eaglebank.user.api.model.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestApplication.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_ShouldReturn201_WhenValidRequest() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "John Doe",
                new Address("Line 1", "Line 2", null, "Town", "County", "POST1"),
                "+1234567890",
                "john@example.com",
                "password123"
        );

        UserResponse response = new UserResponse(
                "usr-123",
                "John Doe",
                request.address(),
                request.phoneNumber(),
                request.email(),
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        when(userService.createUser(ArgumentMatchers.any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("usr-123"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.address.line1").value("Line 1"));
    }

    @Test
    void createUser_ShouldReturn400_WhenNameMissing() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "",
                new Address("Line 1", "Line 2", null, "Town", "County", "POST1"),
                "+1234567890",
                "john@example.com",
                "password123"
        );

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_ShouldReturn200_WhenUserExists() throws Exception {
        UserResponse response = new UserResponse(
                "usr-123",
                "John Doe",
                new Address("Line 1", "Line 2", null, "Town", "County", "POST1"),
                "+1234567890",
                "john@example.com",
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        when(userService.getUserById("usr-123")).thenReturn(response);

        mockMvc.perform(get("/v1/users/usr-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("usr-123"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}
