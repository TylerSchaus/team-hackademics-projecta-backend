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
import com.hackademics.dto.GradeDto;
import com.hackademics.dto.GradeUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.Grade;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GradeRepository gradeRepository;

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
    private Grade grade;

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        gradeRepository.deleteAll();
        courseRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        admin = new User("Admin", "User", "admin@example.com", passwordEncoder.encode("adminPass"), Role.ADMIN, 100L);
        admin = userRepository.save(admin);

        student1 = new User("Student1", "User", "student1@example.com", passwordEncoder.encode("studentPass"), Role.STUDENT, 123L);
        student1 = userRepository.save(student1);

        student2 = new User("Student2", "User", "student2@example.com", passwordEncoder.encode("studentPass"), Role.STUDENT, 456L);
        student2 = userRepository.save(student2);

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

        // Create test grade
        grade = new Grade(student1, course, 85.0);
        grade = gradeRepository.save(grade);
    }

    // Essential endpoints tests
    @Test
    void shouldAllowAdminToCreateGrade() throws Exception {
        GradeDto gradeDto = new GradeDto(student1.getStudentId(), course.getId(), 90.0);

        mockMvc.perform(post("/api/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student.studentId").value(student1.getStudentId()))
                .andExpect(jsonPath("$.course.id").value(course.getId()))
                .andExpect(jsonPath("$.grade").value(90.0));
    }

    @Test
    void shouldNotAllowStudentToCreateGrade() throws Exception {
        GradeDto gradeDto = new GradeDto(student1.getStudentId(), course.getId(), 90.0);

        mockMvc.perform(post("/api/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeDto))
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAdminToUpdateGrade() throws Exception {
        GradeUpdateDto gradeUpdateDto = new GradeUpdateDto(95.0);

        mockMvc.perform(put("/api/grades/" + grade.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeUpdateDto))
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(95.0));
    }

    @Test
    void shouldNotAllowStudentToUpdateGrade() throws Exception {
        GradeUpdateDto gradeUpdateDto = new GradeUpdateDto(95.0);

        mockMvc.perform(put("/api/grades/" + grade.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeUpdateDto))
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowStudentToViewTheirOwnGrade() throws Exception {
        mockMvc.perform(get("/api/grades/" + grade.getId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student.studentId").value(student1.getStudentId()))
                .andExpect(jsonPath("$.course.id").value(course.getId()))
                .andExpect(jsonPath("$.grade").value(85.0));
    }

    @Test
    void shouldNotAllowStudentToViewOtherStudentGrade() throws Exception {
        mockMvc.perform(get("/api/grades/" + grade.getId())
                .header("Authorization", "Bearer " + generateToken(student2)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAdminToViewAnyGrade() throws Exception {
        mockMvc.perform(get("/api/grades/" + grade.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student.studentId").value(student1.getStudentId()))
                .andExpect(jsonPath("$.course.id").value(course.getId()))
                .andExpect(jsonPath("$.grade").value(85.0));
    }

    @Test
    void shouldAllowAdminToDeleteGrade() throws Exception {
        mockMvc.perform(delete("/api/grades/" + grade.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowStudentToDeleteGrade() throws Exception {
        mockMvc.perform(delete("/api/grades/" + grade.getId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowStudentToViewTheirOwnGrades() throws Exception {
        mockMvc.perform(get("/api/grades/student/" + student1.getStudentId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].student.studentId").value(student1.getStudentId()));
    }

    @Test
    void shouldNotAllowStudentToViewOtherStudentGrades() throws Exception {
        mockMvc.perform(get("/api/grades/student/" + student2.getStudentId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAdminToViewAnyStudentGrades() throws Exception {
        mockMvc.perform(get("/api/grades/student/" + student1.getStudentId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].student.studentId").value(student1.getStudentId()));
    }

    @Test
    void shouldAllowAdminToGetAllGrades() throws Exception {
        mockMvc.perform(get("/api/grades")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldNotAllowStudentToGetAllGrades() throws Exception {
        mockMvc.perform(get("/api/grades")
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isUnauthorized());
    }
}
