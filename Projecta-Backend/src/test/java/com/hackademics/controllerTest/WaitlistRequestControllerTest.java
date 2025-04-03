package com.hackademics.controllerTest;

import java.time.LocalDateTime;

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
import com.hackademics.dto.RequestDto.WaitlistRequestDto;
import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistRequest;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.repository.WaitlistRequestRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class WaitlistRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private WaitlistRequestRepository waitlistRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private User admin, student1, student2;
    private Course course;
    private Subject subject;
    private Waitlist waitlist;
    private WaitlistRequest waitlistRequest;

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        // Create and save users
        admin = new User("Admin", "User", "admin@example.com", "1234567890", passwordEncoder.encode("adminPass"), Role.ADMIN, 1000L);
        student1 = new User("Student", "One", "student1@example.com", "1234567891", passwordEncoder.encode("studentPass"), Role.STUDENT, 1001L);
        student2 = new User("Student", "Two", "student2@example.com", "1234567892", passwordEncoder.encode("studentPass"), Role.STUDENT, 1002L);
        admin = userRepository.save(admin);
        student1 = userRepository.save(student1);
        student2 = userRepository.save(student2);

        // Create and save subject
        subject = new Subject("Test Subject", "TEST");
        subject = subjectRepository.save(subject);

        // Create and save course
        course = new Course(admin, subject, "Test Course", LocalDateTime.now(), 
            LocalDateTime.now().plusMonths(6), 10, "TEST101", null, null, null);
        course = courseRepository.save(course);

        // Create and save waitlist
        waitlist = new Waitlist(course, 10);
        waitlist = waitlistRepository.save(waitlist);

        // Create and save waitlist request
        waitlistRequest = new WaitlistRequest(waitlist, student1);
        waitlistRequest = waitlistRequestRepository.save(waitlistRequest);
    }

    @Test
    void shouldAllowStudentToCreateWaitlistRequest() throws Exception {
        WaitlistRequestDto requestDto = new WaitlistRequestDto(waitlist.getId(), student1.getStudentId());

        mockMvc.perform(post("/api/waitlist-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitlist.waitlistId").value(waitlist.getId()))
                .andExpect(jsonPath("$.student.studentId").value(student1.getStudentId()));
    }

    @Test
    void shouldNotAllowStudentToCreateRequestForAnotherStudent() throws Exception {
        WaitlistRequestDto requestDto = new WaitlistRequestDto(waitlist.getId(), student2.getStudentId());

        mockMvc.perform(post("/api/waitlist-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToGetAllWaitlistRequests() throws Exception {
        mockMvc.perform(get("/api/waitlist-requests")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].waitlist.waitlistId").value(waitlist.getId()))
                .andExpect(jsonPath("$[0].student.studentId").value(student1.getStudentId()));
    }

    @Test
    void shouldAllowStudentToDeleteOwnRequest() throws Exception {
        mockMvc.perform(delete("/api/waitlist-requests/" + waitlistRequest.getId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldAllowAdminToDeleteAnyRequest() throws Exception {
        mockMvc.perform(delete("/api/waitlist-requests/" + waitlistRequest.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowStudentToDeleteAnotherStudentsRequest() throws Exception {
        mockMvc.perform(delete("/api/waitlist-requests/" + waitlistRequest.getId())
                .header("Authorization", "Bearer " + generateToken(student2)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404ForNonExistentRequest() throws Exception {
        mockMvc.perform(delete("/api/waitlist-requests/999999")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401ForUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/waitlist-requests"))
                .andExpect(status().isUnauthorized());
    }
}
