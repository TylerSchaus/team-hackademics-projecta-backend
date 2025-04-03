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
import com.hackademics.dto.RequestDto.CourseDto;
import com.hackademics.dto.UpdateDto.CourseUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        admin = new User("Admin", "User", "admin@example.com", "2317658909", passwordEncoder.encode("adminPass"), Role.ADMIN, 100L);
        admin = userRepository.save(admin);

        student = new User("Student", "User", "student@example.com", "2317658909", passwordEncoder.encode("studentPass"), Role.STUDENT, 123L);
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
    }

    // Public endpoints tests
    @Test
    void shouldAllowAnyoneToGetAllActiveCourses() throws Exception {
        mockMvc.perform(get("/api/courses/active")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldAllowAnyoneToGetAllUpcomingCourses() throws Exception {
        mockMvc.perform(get("/api/courses/upcoming")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].courseName").value("Introduction to Programming"))
                .andExpect(jsonPath("$[0].courseNumber").value("101"));
    }

    @Test
    void shouldAllowAnyoneToGetAllCoursesBySubjectId() throws Exception {
        mockMvc.perform(get("/api/courses/subject/" + subject.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].courseName").value("Introduction to Programming"))
                .andExpect(jsonPath("$[0].courseNumber").value("101"));
    }

    // RBA endpoints tests
    @Test
    void shouldAllowAdminToGetAllCoursesByAdmin() throws Exception {
        mockMvc.perform(get("/api/courses/admin")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].courseName").value("Introduction to Programming"))
                .andExpect(jsonPath("$[0].courseNumber").value("101"));
    }

    @Test
    void shouldNotAllowStudentToGetAllCoursesByAdmin() throws Exception {
        mockMvc.perform(get("/api/courses/admin")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToCreateCourse() throws Exception {
        CourseDto courseDto = new CourseDto(
            admin.getAdminId(),
            subject.getId(),
            "Advanced Programming",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusMonths(4),
            50,
            "201",
            1,
            LocalTime.of(11, 0),
            LocalTime.of(12, 30)
        );

        mockMvc.perform(post("/api/courses")
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value("Advanced Programming"))
                .andExpect(jsonPath("$.courseNumber").value("201"));
    }

    @Test
    void shouldNotAllowStudentToCreateCourse() throws Exception {
        CourseDto courseDto = new CourseDto(
            admin.getId(),
            subject.getId(),
            "Advanced Programming",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusMonths(4),
            50,
            "201",
            1,
            LocalTime.of(11, 0),
            LocalTime.of(12, 30)
        );

        mockMvc.perform(post("/api/courses")
                .header("Authorization", "Bearer " + generateToken(student))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToUpdateCourse() throws Exception {
        CourseUpdateDto courseUpdateDto = new CourseUpdateDto();
        courseUpdateDto.setCourseName("Updated Course Name");

        mockMvc.perform(put("/api/courses/" + course.getId())
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value("Updated Course Name"));
    }

    @Test
    void shouldNotAllowStudentToUpdateCourse() throws Exception {
        CourseUpdateDto courseUpdateDto = new CourseUpdateDto();
        courseUpdateDto.setCourseName("Updated Course Name");

        mockMvc.perform(put("/api/courses/" + course.getId())
                .header("Authorization", "Bearer " + generateToken(student))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseUpdateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToDeleteCourse() throws Exception {
        mockMvc.perform(delete("/api/courses/" + course.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowStudentToDeleteCourse() throws Exception {
        mockMvc.perform(delete("/api/courses/" + course.getId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }
}
