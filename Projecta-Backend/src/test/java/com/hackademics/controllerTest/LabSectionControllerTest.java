package com.hackademics.controllerTest;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
import com.hackademics.dto.RequestDto.LabSectionDto;
import com.hackademics.model.Course;
import com.hackademics.model.LabSection;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class LabSectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private User admin;
    private User student;
    private Subject subject;
    private Course course;
    private LabSection labSection;

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        labSectionRepository.deleteAll();
        courseRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        admin = new User("Admin", "User", "admin@example.com", passwordEncoder.encode("adminPass"), Role.ADMIN, 100L);
        admin = userRepository.save(admin);

        student = new User("Student", "User", "student@example.com", passwordEncoder.encode("studentPass"), Role.STUDENT, 123L);
        student = userRepository.save(student);

        // Create test subject
        subject = new Subject("Computer Science", "COSC");
        subject = subjectRepository.save(subject);

        // Create test course
        course = new Course(
            admin,
            subject,
            "Introduction to Programming",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusMonths(4),
            50,
            "101",
            1,
            LocalTime.of(9, 0),
            LocalTime.of(10, 30)
        );
        course = courseRepository.save(course);

        // Create test lab section
        labSection = new LabSection(1L, 20, course, 1, LocalTime.of(14, 0), LocalTime.of(15, 30));
        labSection = labSectionRepository.save(labSection);
    }

    @Test
    void shouldAllowAdminToCreateLabSection() throws Exception {
        LabSectionDto labSectionDto = new LabSectionDto(
            course.getId(),
            25,
            1,
            LocalTime.of(14, 0),
            LocalTime.of(15, 30)
        );

        mockMvc.perform(post("/api/lab-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labSectionDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.capacity").value(25))
                .andExpect(jsonPath("$.sectionId").value(2)); // Should be second lab section
    }

    @Test
    void shouldNotAllowStudentToCreateLabSection() throws Exception {
        LabSectionDto labSectionDto = new LabSectionDto(
            course.getId(),
            25,
            1,
            LocalTime.of(14, 0),
            LocalTime.of(15, 30)
        );

        mockMvc.perform(post("/api/lab-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labSectionDto))
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnLabSectionById() throws Exception {
        mockMvc.perform(get("/api/lab-sections/" + labSection.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(labSection.getId()))
                .andExpect(jsonPath("$.capacity").value(20))
                .andExpect(jsonPath("$.sectionId").value(1));
    }

    @Test
    void shouldReturn404ForNonExistentLabSection() throws Exception {
        mockMvc.perform(get("/api/lab-sections/999")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnLabSectionsByCourseId() throws Exception {
        mockMvc.perform(get("/api/lab-sections/course/" + course.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(labSection.getId()));
    }

    @Test
    void shouldReturn404ForNonExistentCourse() throws Exception {
        mockMvc.perform(get("/api/lab-sections/course/999")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidLabSectionData() throws Exception {
        LabSectionDto labSectionDto = new LabSectionDto(
            course.getId(),
            -1, // Invalid capacity
            1,
            LocalTime.of(14, 0),
            LocalTime.of(15, 30)
        );

        mockMvc.perform(post("/api/lab-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labSectionDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400ForInvalidTimeRange() throws Exception {
        LabSectionDto labSectionDto = new LabSectionDto(
            course.getId(),
            25,
            1,
            LocalTime.of(15, 30), // End time before start time
            LocalTime.of(14, 0)
        );

        mockMvc.perform(post("/api/lab-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labSectionDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isBadRequest());
    }

} 