package com.hackademics.authTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackademics.dto.LoginDto;
import com.hackademics.dto.SignUpDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        
        userRepository.deleteAll();
        
    }

    // Comment for testing. 
    @Test
    void shouldRegisterNewUserSuccessfully() throws Exception {
        SignUpDto signUpDto = new SignUpDto("John", "Doe", "john@example.com", "password123", Role.STUDENT);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Manually create and save a user since login requires an existing user
        User user = new User("John", "Doe", "john@example.com", passwordEncoder.encode("password123"), Role.STUDENT, 123L);
        userRepository.save(user);

        // Create login request
        LoginDto loginDto = new LoginDto("john@example.com", "password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty()); // Ensure token is returned
    }

    @Test
    void shouldLoginUnsuccessfully() throws Exception {
        // Manually create and save a user since login requires an existing user
        User user = new User("John", "Doe", "john@example.com", passwordEncoder.encode("password123"), Role.STUDENT, 123L);
        userRepository.save(user);

        // Create login request with wrong password
        LoginDto loginDto = new LoginDto("john@example.com", "password321"); // Incorrect password

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailRegistrationWithInvalidEmail() throws Exception {
        SignUpDto signUpDto = new SignUpDto("John", "Doe", "invalid-email", "password123", Role.STUDENT);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    void shouldFailRegistrationWithDuplicateEmail() throws Exception {
        // Existing user
        User user = new User("John", "Doe", "john@example.com", passwordEncoder.encode("password123"), Role.STUDENT, 123L);
        userRepository.save(user);

        // New signup request with duplicate email
        SignUpDto signUpDto = new SignUpDto("Jane", "Doe", "john@example.com", "password456", Role.STUDENT);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    void shouldFailRegistrationWithNoRole() throws Exception {
        SignUpDto signUpDto = new SignUpDto("John", "Doe", "john@example.com", "password123", null); // Role is missing

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isBadRequest()); 
    }

    @Test
    void shouldDenyAccessToProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me")) // No token provided
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDenyAccessToProtectedEndpointWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }
}
