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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackademics.dto.RequestDto.WaitlistEnrollmentDto;
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
    private User student1;
    private User student2;
    private Subject subject;
    private Course course;
    private Waitlist waitlist;
    private WaitlistEnrollment enrollment1;

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
        admin = new User("Admin", "User", "admin@example.com", "2317658909", passwordEncoder.encode("adminPass"), Role.ADMIN, 100L);
        admin = userRepository.save(admin);

        student1 = new User("Student1", "User", "student1@example.com", "2317658909", passwordEncoder.encode("studentPass"), Role.STUDENT, 123L);
        student1 = userRepository.save(student1);

        student2 = new User("Student2", "User", "student2@example.com", "2317658909", passwordEncoder.encode("studentPass"), Role.STUDENT, 456L);
        student2 = userRepository.save(student2);

        // Create test subject
        subject = new Subject("Computer Science", "COSC");
        subject = subjectRepository.save(subject);

        // Create test course
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

        // Create test waitlist
        waitlist = new Waitlist(course, 10);
        waitlist = waitlistRepository.save(waitlist);

        // Create test enrollment only for student1
        enrollment1 = new WaitlistEnrollment(1, waitlist, student1);
        enrollment1 = waitlistEnrollmentRepository.save(enrollment1);
    }

    @Test
    void shouldAllowAdminToCreateWaitlistEnrollment() throws Exception {
        WaitlistEnrollmentDto enrollmentDto = new WaitlistEnrollmentDto(waitlist.getId(), student2.getStudentId());

        mockMvc.perform(post("/api/waitlist-enrollments")
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.waitlistPosition").value(2))
                .andExpect(jsonPath("$.studentSummaryDto.studentId").value(student2.getStudentId()));
    }

    @Test
    void shouldAllowStudentToCreateTheirOwnWaitlistEnrollment() throws Exception {
        WaitlistEnrollmentDto enrollmentDto = new WaitlistEnrollmentDto(waitlist.getId(), student2.getStudentId());

        mockMvc.perform(post("/api/waitlist-enrollments")
                .header("Authorization", "Bearer " + generateToken(student2))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.waitlistPosition").value(2))
                .andExpect(jsonPath("$.studentSummaryDto.studentId").value(student2.getStudentId()));
    }

    @Test
    void shouldNotAllowStudentToCreateWaitlistEnrollmentForAnotherStudent() throws Exception {
        WaitlistEnrollmentDto enrollmentDto = new WaitlistEnrollmentDto(waitlist.getId(), student2.getStudentId());

        mockMvc.perform(post("/api/waitlist-enrollments")
                .header("Authorization", "Bearer " + generateToken(student1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAdminToDeleteWaitlistEnrollment() throws Exception {
        mockMvc.perform(delete("/api/waitlist-enrollments/" + enrollment1.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldAllowStudentToDeleteTheirOwnWaitlistEnrollment() throws Exception {
        mockMvc.perform(delete("/api/waitlist-enrollments/" + enrollment1.getId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldUpdatePositionsWhenDeletingWaitlistEnrollment() throws Exception {
        // First create an enrollment for student2
        WaitlistEnrollmentDto enrollmentDto = new WaitlistEnrollmentDto(waitlist.getId(), student2.getStudentId());
        mockMvc.perform(post("/api/waitlist-enrollments")
                .header("Authorization", "Bearer " + generateToken(student2))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDto)))
                .andExpect(status().isCreated());

        // Delete first enrollment
        mockMvc.perform(delete("/api/waitlist-enrollments/" + enrollment1.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isNoContent());

        // Verify that student2's position was updated to 1
        mockMvc.perform(get("/api/waitlist-enrollments/student/" + student2.getStudentId())
                .header("Authorization", "Bearer " + generateToken(student2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].waitlistPosition").value(1));
    }

    @Test
    void shouldAllowAdminToGetStudentWaitlistEnrollments() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments/student/" + student1.getStudentId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentSummaryDto.studentId").value(student1.getStudentId()));
    }

    @Test
    void shouldAllowStudentToGetTheirOwnWaitlistEnrollments() throws Exception {
        mockMvc.perform(get("/api/waitlist-enrollments/student/" + student1.getStudentId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentSummaryDto.studentId").value(student1.getStudentId()));
    }
}
