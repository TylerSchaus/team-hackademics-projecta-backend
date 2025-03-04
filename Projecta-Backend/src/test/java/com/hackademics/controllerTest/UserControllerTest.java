package com.hackademics.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackademics.dto.UserUpdateDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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

        admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("adminPass"));
        admin.setRole(Role.ADMIN);
        admin = userRepository.save(admin);

        student = new User();
        student.setFirstName("Student");
        student.setLastName("User");
        student.setEmail("student@example.com");
        student.setPassword(passwordEncoder.encode("studentPass"));
        student.setRole(Role.STUDENT);
        student.setStudentId(123L);
        student = userRepository.save(student);

        anotherStudent = new User();
        anotherStudent.setFirstName("Another");
        anotherStudent.setLastName("Student");
        anotherStudent.setEmail("anotherstudent@example.com");
        anotherStudent.setPassword(passwordEncoder.encode("studentPass"));
        anotherStudent.setRole(Role.STUDENT);
        anotherStudent.setStudentId(456L);
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
    void shouldNotAllowStudentToUpdateOwnName() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFirstName("NewName");

        mockMvc.perform(put("/api/users/" + student.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden()); // Expecting 403 Forbidden
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

}
