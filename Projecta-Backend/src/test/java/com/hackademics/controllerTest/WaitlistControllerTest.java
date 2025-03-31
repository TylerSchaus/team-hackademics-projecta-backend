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
import com.hackademics.dto.RequestDto.WaitlistDto;
import com.hackademics.dto.UpdateDto.WaitlistUpdateDto;
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
    private Course course2;
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


         course = new Course(
            admin,
            subject,
            "Introduction to Programming",
            java.time.LocalDateTime.now().plusDays(1),
            java.time.LocalDateTime.now().plusMonths(4),
            50,
            "101",
            1,
            java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(10, 30)
        );
        course = courseRepository.save(course);

        // Create test course without a waitlist 
        course2 = new Course( 
            admin,
            subject,
            "Introduction to Programming II",
            java.time.LocalDateTime.now().plusDays(1),
            java.time.LocalDateTime.now().plusMonths(4),
            50,
            "101",
            1,
            java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(10, 30)
        );
        course2 = courseRepository.save(course2);

        // Create test waitlist
        waitlist = new Waitlist(course, 10);
        waitlist = waitlistRepository.save(waitlist);
    }

    @Test
    void shouldAllowAdminToCreateWaitlist() throws Exception {
        WaitlistDto waitlistDto = new WaitlistDto(course2.getId(), 15);

        mockMvc.perform(post("/api/waitlists")
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(waitlistDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.capacity").value(15))
                .andExpect(jsonPath("$.course.courseName").value("Introduction to Programming II"));
    }

    @Test
    void shouldNotAllowStudentToCreateWaitlist() throws Exception {
        WaitlistDto waitlistDto = new WaitlistDto(course.getId(), 15);

        mockMvc.perform(post("/api/waitlists")
                .header("Authorization", "Bearer " + generateToken(student))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(waitlistDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAdminToGetAllWaitlists() throws Exception {
        mockMvc.perform(get("/api/waitlists")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].capacity").value(10))
                .andExpect(jsonPath("$[0].course.courseName").value("Introduction to Programming"));
    }

    @Test
    void shouldNotAllowStudentToGetAllWaitlists() throws Exception {
        mockMvc.perform(get("/api/waitlists")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAdminToGetWaitlistById() throws Exception {
        mockMvc.perform(get("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(10))
                .andExpect(jsonPath("$.course.courseName").value("Introduction to Programming"));
    }

    @Test
    void shouldNotAllowStudentToGetWaitlistById() throws Exception {
        mockMvc.perform(get("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAdminToUpdateWaitlist() throws Exception {
        WaitlistUpdateDto waitlistUpdateDto = new WaitlistUpdateDto(20);

        mockMvc.perform(put("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(waitlistUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(20));
    }

    @Test
    void shouldNotAllowStudentToUpdateWaitlist() throws Exception {
        WaitlistUpdateDto waitlistUpdateDto = new WaitlistUpdateDto(20);

        mockMvc.perform(put("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(student))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(waitlistUpdateDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAdminToDeleteWaitlist() throws Exception {
        mockMvc.perform(delete("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowStudentToDeleteWaitlist() throws Exception {
        mockMvc.perform(delete("/api/waitlists/" + waitlist.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isUnauthorized());
    }
}
