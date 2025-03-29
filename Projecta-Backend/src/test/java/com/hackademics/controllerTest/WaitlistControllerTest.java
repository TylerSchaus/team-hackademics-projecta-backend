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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class WaitlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void shouldAllowAdminToCreateWaitlist() throws Exception {
        Waitlist newWaitlist = new Waitlist(course, 15);

        mockMvc.perform(post("/api/waitlists")
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newWaitlist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitlistLimit").value(15))
                .andExpect(jsonPath("$.course.id").value(course.getId()));
    }

    @Test
    void shouldNotAllowStudentToCreateWaitlist() throws Exception {
        Waitlist newWaitlist = new Waitlist(course, 15);

        mockMvc.perform(post("/api/waitlists")
                .header("Authorization", "Bearer " + generateToken(student))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newWaitlist)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToGetAllWaitlists() throws Exception {
        mockMvc.perform(get("/api/waitlists")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].waitlistLimit").value(10));
    }

    @Test
    void shouldNotAllowStudentToGetAllWaitlists() throws Exception {
        mockMvc.perform(get("/api/waitlists")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToGetWaitlistById() throws Exception {
        mockMvc.perform(get("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitlistLimit").value(10))
                .andExpect(jsonPath("$.course.id").value(course.getId()));
    }

    @Test
    void shouldNotAllowStudentToGetWaitlistById() throws Exception {
        mockMvc.perform(get("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToUpdateWaitlist() throws Exception {
        waitlist.setWaitlistLimit(20);
        waitlistRepository.save(waitlist);

        mockMvc.perform(put("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(waitlist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitlistLimit").value(20));
    }

    @Test
    void shouldNotAllowStudentToUpdateWaitlist() throws Exception {
        waitlist.setWaitlistLimit(20);
        waitlistRepository.save(waitlist);

        mockMvc.perform(put("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(student))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(waitlist)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToDeleteWaitlist() throws Exception {
        mockMvc.perform(delete("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowStudentToDeleteWaitlist() throws Exception {
        mockMvc.perform(delete("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }
} 