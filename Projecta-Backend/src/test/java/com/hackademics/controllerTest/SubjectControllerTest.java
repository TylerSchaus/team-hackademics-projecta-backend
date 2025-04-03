package com.hackademics.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User admin;
    private User student;

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        subjectRepository.deleteAll(); // Clean database before each test
        userRepository.deleteAll();

        // Create test users
        admin = new User("Admin", "User", "admin@example.com", "2317658909", passwordEncoder.encode("adminPass"), Role.ADMIN, 100L);
        admin = userRepository.save(admin);

        student = new User("Student", "User", "student@example.com", "2317658909", passwordEncoder.encode("studentPass"), Role.STUDENT, 123L);
        student = userRepository.save(student);

        // Create test subjects
        Subject subject1 = new Subject("Computer Science", "CS");
        subjectRepository.save(subject1);

        Subject subject2 = new Subject("Mathematics", "MATH");
        subjectRepository.save(subject2);
    }

    @Test
    void shouldAllowStudentToGetAllSubjects() throws Exception {
        mockMvc.perform(get("/api/subjects")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].subjectName").value("Computer Science"))
                .andExpect(jsonPath("$[0].subjectTag").value("CS"))
                .andExpect(jsonPath("$[1].subjectName").value("Mathematics"))
                .andExpect(jsonPath("$[1].subjectTag").value("MATH"));
    }

    @Test
    void shouldAllowAdminToGetAllSubjects() throws Exception {
        mockMvc.perform(get("/api/subjects")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].subjectName").value("Computer Science"))
                .andExpect(jsonPath("$[0].subjectTag").value("CS"))
                .andExpect(jsonPath("$[1].subjectName").value("Mathematics"))
                .andExpect(jsonPath("$[1].subjectTag").value("MATH"));
    }
}
