package com.hackademics.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackademics.dto.RequestDto.AdminSignUpDto;
import com.hackademics.dto.UpdateDto.UserUpdateDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private User student, admin, anotherStudent;

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Clean database before each test

        admin = new User("Admin", "User", "admin@example.com", "2317658909", passwordEncoder.encode("adminPass"), Role.ADMIN, 100L);
        admin = userRepository.save(admin);

        student = new User("Student", "User", "student@example.com", "2317658909", passwordEncoder.encode("studentPass"), Role.STUDENT, 123L);
        student = userRepository.save(student);

        anotherStudent = new User("Another", "Student", "anotherstudent@example.com", "2317658909", passwordEncoder.encode("studentPass"), Role.STUDENT, 456L);
        anotherStudent = userRepository.save(anotherStudent);
    }

    @Test
    void shouldAllowStudentToUpdateOwnEmail() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("newemail@example.com");

        mockMvc.perform(put("/api/users/" + student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"));
    }

    @Test
    void shouldAllowStudentToUpdateOwnName() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFirstName("NewName");

        mockMvc.perform(put("/api/users/" + student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewName"));
    }

    @Test
    void shouldAllowAdminToUpdateStudentName() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFirstName("UpdatedName");

        mockMvc.perform(put("/api/users/" + student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedName"));
    }

    @Test
    void shouldAllowAdminToUpdateStudentIdIfAvailable() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setStudentId(999L); // A new, available ID

        mockMvc.perform(put("/api/users/" + student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(999L));
    }

    @Test
    void shouldNotAllowAdminToUpdateStudentIdIfTaken() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setStudentId(456L); // Already taken by anotherStudent

        mockMvc.perform(put("/api/users/" + student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isConflict());

    }

    @Test
    void shouldNotAllowStudentToUpdateAnotherStudentsEmail() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("unauthorized@example.com");

        mockMvc.perform(put("/api/users/" + anotherStudent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden()); // Expecting 403 Forbidden
    }

    @Test
    void shouldAllowAdminToDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + student.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isNoContent()); // 204 No Content means successful delete
    }

    @Test
    void shouldNotAllowStudentToDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + admin.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    void shouldAllowAdminToGetUsersByRole() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("role", "STUDENT")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // Should return 2 students
    }

    @Test
    void shouldNotAllowStudentToGetUsersByRole() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("role", "STUDENT")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    void shouldAllowUserToRetrieveTheirOwnInfo() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(student.getEmail()));
    }

    @Test
    void shouldAllowAdminToGetStudentsByGradeRange() throws Exception {
        mockMvc.perform(get("/api/users/grade-range")
                .param("low", "70")
                .param("high", "90")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldNotAllowStudentToGetStudentsByGradeRange() throws Exception {
        mockMvc.perform(get("/api/users/grade-range")
                .param("low", "70")
                .param("high", "90")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToGetStudentsByNamePrefix() throws Exception {
        mockMvc.perform(get("/api/users/name-prefix")
                .param("prefix", "Stu")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("Student"));
    }

    @Test
    void shouldNotAllowStudentToGetStudentsByNamePrefix() throws Exception {
        mockMvc.perform(get("/api/users/name-prefix")
                .param("prefix", "Stu")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    // Admin signup tests
    @Test
    void shouldAllowAdminToRegisterNewUser() throws Exception {
        AdminSignUpDto signUpDto = new AdminSignUpDto(
            "New", "User", "newuser@example.com", "2317658909", Role.STUDENT);

        mockMvc.perform(post("/api/users/admin-signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void shouldNotAllowStudentToRegisterNewUser() throws Exception {
        AdminSignUpDto signUpDto = new AdminSignUpDto(
            "New", "User", "newuser@example.com", "2317658909", Role.STUDENT);

        mockMvc.perform(post("/api/users/admin-signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto))
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotAllowRegistrationWithDuplicateEmail() throws Exception {
        AdminSignUpDto signUpDto = new AdminSignUpDto(
            "New", "User", "student@example.com", "2317658909", Role.STUDENT);

        mockMvc.perform(post("/api/users/admin-signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Email is already in use"));
    }

    @Test
    void shouldNotAllowRegistrationWithInvalidEmail() throws Exception {
        AdminSignUpDto signUpDto = new AdminSignUpDto(
            "New", "User", "invalid-email", "2317658909", Role.STUDENT);

        mockMvc.perform(post("/api/users/admin-signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotAllowRegistrationWithMissingRequiredFields() throws Exception {
        AdminSignUpDto signUpDto = new AdminSignUpDto(
            "", "", "", "2317658909", null);

        mockMvc.perform(post("/api/users/admin-signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAllowAdminToRegisterAnotherAdmin() throws Exception {
        AdminSignUpDto signUpDto = new AdminSignUpDto(
            "New", "Admin", "newadmin@example.com", "2317658909", Role.ADMIN);

        mockMvc.perform(post("/api/users/admin-signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newadmin@example.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("Admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.adminId").exists());
    }
}
