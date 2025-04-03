package com.hackademics.controllerTest;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.Grade;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class StudentRecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User admin;
    private User student;
    private Subject subject;
    private Course course;
    private Enrollment enrollment;
    private Grade grade;

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
        enrollmentRepository.deleteAll();
        gradeRepository.deleteAll();

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

        // Create enrollment for the student
        enrollment = new Enrollment(course, student, null);
        enrollment = enrollmentRepository.save(enrollment);
        
        // Set up bidirectional relationship
        student.getEnrollments().add(enrollment);
        student = userRepository.save(student);

        // Create a grade for the student
        grade = new Grade(student, course, 85.5);
        grade = gradeRepository.save(grade);
        
        // Force a refresh of the student to ensure collections are loaded
        student = userRepository.findByStudentId(student.getStudentId()).orElseThrow();
    }

    @Test
    void shouldAllowStudentToViewTheirOwnRecord() throws Exception {
        System.out.println("Enrollments: " + student.getEnrollments().size());
        System.out.println("Course term: " + course.getTerm());
        System.out.println("Enrollment term: " + enrollment.getTerm());

        mockMvc.perform(get("/api/student-records/" + student.getStudentId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentName").value("Student User"))
                .andExpect(jsonPath("$.studentNumber").value(student.getStudentId()))
                .andExpect(jsonPath("$.currentEnrollments.length()").value(1))
                .andExpect(jsonPath("$.currentEnrollments[0].courseTag").value(course.getCourseTag()))
                .andExpect(jsonPath("$.currentEnrollments[0].courseName").value(course.getCourseName()))
                .andExpect(jsonPath("$.currentEnrollments[0].term").value(course.getTerm()))
                .andExpect(jsonPath("$.completedCourses.length()").value(1))
                .andExpect(jsonPath("$.completedCourses[0].courseTag").value(course.getCourseTag()))
                .andExpect(jsonPath("$.completedCourses[0].courseName").value(course.getCourseName()))
                .andExpect(jsonPath("$.completedCourses[0].grade").value(85.5))
                .andExpect(jsonPath("$.academicPerformance.completedCoursesCount").value(1))
                .andExpect(jsonPath("$.academicPerformance.gpa").value(3.9))
                .andExpect(jsonPath("$.academicPerformance.percentage").value(85.5));
    }

    @Test
    void shouldAllowAdminToViewAnyStudentRecord() throws Exception {
        mockMvc.perform(get("/api/student-records/" + student.getStudentId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentName").value("Student User"))
                .andExpect(jsonPath("$.studentNumber").value(student.getStudentId()))
                .andExpect(jsonPath("$.currentEnrollments.length()").value(1))
                .andExpect(jsonPath("$.currentEnrollments[0].courseTag").value(course.getCourseTag()))
                .andExpect(jsonPath("$.currentEnrollments[0].courseName").value(course.getCourseName()))
                .andExpect(jsonPath("$.currentEnrollments[0].term").value(course.getTerm()))
                .andExpect(jsonPath("$.completedCourses.length()").value(1))
                .andExpect(jsonPath("$.completedCourses[0].courseTag").value(course.getCourseTag()))
                .andExpect(jsonPath("$.completedCourses[0].courseName").value(course.getCourseName()))
                .andExpect(jsonPath("$.completedCourses[0].grade").value(85.5))
                .andExpect(jsonPath("$.academicPerformance.completedCoursesCount").value(1))
                .andExpect(jsonPath("$.academicPerformance.gpa").value(3.9))
                .andExpect(jsonPath("$.academicPerformance.percentage").value(85.5));
    }

    @Test
    void shouldNotAllowStudentToViewOtherStudentRecord() throws Exception {
        // Create another student
        User otherStudent = new User("Other", "Student", "other@example.com", "2317658909", passwordEncoder.encode("otherPass"), Role.STUDENT, 456L);
        otherStudent = userRepository.save(otherStudent);

        mockMvc.perform(get("/api/student-records/" + otherStudent.getStudentId())
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404ForNonExistentStudent() throws Exception {
        mockMvc.perform(get("/api/student-records/999999")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401ForUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/student-records/" + student.getStudentId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowStudentToExportTheirRecordAsText() throws Exception {
        mockMvc.perform(get("/api/student-records/" + student.getStudentId() + "/text")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    // Verify essential content without being strict about formatting
                    assert content.contains("Student User");
                    assert content.contains(String.valueOf(student.getStudentId()));
                    assert content.contains(course.getCourseTag());
                    assert content.contains(course.getCourseName());
                    assert content.contains(course.getCourseNumber());
                    assert content.contains(course.getTerm());
                    assert content.contains("86"); // Grade
                    assert content.contains("3.9"); // GPA
                });
    }

    @Test
    void shouldAllowAdminToExportStudentRecordAsText() throws Exception {
        mockMvc.perform(get("/api/student-records/" + student.getStudentId() + "/text")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    // Verify essential content without being strict about formatting
                    assert content.contains("Student User");
                    assert content.contains(String.valueOf(student.getStudentId()));
                    assert content.contains(course.getCourseTag());
                    assert content.contains(course.getCourseName());
                    assert content.contains(course.getCourseNumber());
                    assert content.contains(course.getTerm());
                    assert content.contains("86"); // Grade
                    assert content.contains("3.9"); // GPA
                });
    }

    @Test
    void shouldAllowStudentToExportTheirRecordAsCsv() throws Exception {
        mockMvc.perform(get("/api/student-records/" + student.getStudentId() + "/csv")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    // Verify essential content without being strict about formatting
                    assert content.contains("Student User");
                    assert content.contains(String.valueOf(student.getStudentId()));
                    assert content.contains(course.getCourseTag());
                    assert content.contains(course.getCourseName());
                    assert content.contains(course.getCourseNumber());
                    assert content.contains(course.getTerm());
                    assert content.contains("86"); // Grade
                    assert content.contains("3.9"); // GPA
                });
    }

    @Test
    void shouldAllowAdminToExportStudentRecordAsCsv() throws Exception {
        mockMvc.perform(get("/api/student-records/" + student.getStudentId() + "/csv")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    // Verify essential content without being strict about formatting
                    assert content.contains("Student User");
                    assert content.contains(String.valueOf(student.getStudentId()));
                    assert content.contains(course.getCourseTag());
                    assert content.contains(course.getCourseName());
                    assert content.contains(course.getCourseNumber());
                    assert content.contains(course.getTerm());
                    assert content.contains("86"); // Grade
                    assert content.contains("3.9"); // GPA
                });
    }

    @Test
    void shouldNotAllowStudentToExportOtherStudentRecord() throws Exception {
        // Create another student
        User otherStudent = new User("Other", "Student", "other@example.com", "2317658909", passwordEncoder.encode("otherPass"), Role.STUDENT, 456L);
        otherStudent = userRepository.save(otherStudent);

        mockMvc.perform(get("/api/student-records/" + otherStudent.getStudentId() + "/text")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/student-records/" + otherStudent.getStudentId() + "/csv")
                .header("Authorization", "Bearer " + generateToken(student)))
                .andExpect(status().isForbidden());
    }
}
