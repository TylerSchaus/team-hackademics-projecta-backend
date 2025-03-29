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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistEnrollmentRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class WaitlistEnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WaitlistEnrollmentRepository waitlistEnrollmentRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

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
    private Waitlist waitlist;
    private WaitlistEnrollment waitlistEnrollment;

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        waitlistEnrollmentRepository.deleteAll();
        waitlistRepository.deleteAll();
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

        // Create test waitlist
        waitlist = new Waitlist(course, 10);
        waitlist = waitlistRepository.save(waitlist);

        // Create test waitlist enrollment
        waitlistEnrollment = new WaitlistEnrollment(1, waitlist, student);
        waitlistEnrollment = waitlistEnrollmentRepository.save(waitlistEnrollment);
    }

    @Test
    void shouldAllowStudentToCreateWaitlistEnrollment() throws Exception {
        WaitlistEnrollment newEnrollment = new WaitlistEnrollment(1, waitlist, student);

        mockMvc.perform(post("/api/waitlist-enrollments")
                .header("Authorization", "Bearer " + generateToken(student))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnrollment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitlist.id").value(waitlist.getId()))
                .andExpect(jsonPath("$.student.id").value(student.getId()));
    }

    @Test
    void shouldNotAllowAdminToCreateWaitlistEnrollment() throws Exception {
        WaitlistEnrollment newEnrollment = new WaitlistEnrollment(1, waitlist, student);

        mockMvc.perform(post("/api/waitlist-enrollments")
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnrollment)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToGetAllWaitlistEnrollments() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].waitlist.id").value(waitlist.getId()))
                .andExpect(jsonPath("$[0].student.id").value(student.getId()));
    }

    @Test
    void shouldNotAllowStudentToGetAllWaitlistEnrollments() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToGetWaitlistEnrollmentById() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments/" + waitlistEnrollment.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitlist.id").value(waitlist.getId()))
                .andExpect(jsonPath("$.student.id").value(student.getId()));
    }

    @Test
    void shouldNotAllowStudentToGetWaitlistEnrollmentById() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments/" + waitlistEnrollment.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToDeleteWaitlistEnrollment() throws Exception {
        mockMvc.perform(delete("/api/waitlist-enrollments/" + waitlistEnrollment.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowStudentToDeleteWaitlistEnrollment() throws Exception {
        mockMvc.perform(delete("/api/waitlist-enrollments/" + waitlistEnrollment.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }
} 