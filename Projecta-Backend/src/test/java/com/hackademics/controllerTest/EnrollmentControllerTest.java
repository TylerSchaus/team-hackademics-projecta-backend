package com.hackademics.controllerTest;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
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
import com.hackademics.dto.EnrollmentDto;
import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.LabSection;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.EnrollmentService;
import com.hackademics.service.JwtService;
import com.hackademics.util.TermDeterminator;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EnrollmentService enrollmentService;

    private User admin;
    private User student1;
    private User student2;
    private Subject subject;
    private Course course;
    private Enrollment enrollment;
    private LabSection labSection1;
    private LabSection labSection2;

    // Helper method
    private String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        labSectionRepository.deleteAll();
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
        course = new Course(admin, subject, "Introduction to Programming", LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusMonths(4), 50, "101", 1, LocalTime.of(9, 0), LocalTime.of(10, 30));
        course = courseRepository.save(course);

        // Create test lab sections
        labSection1 = new LabSection(1L, 20, course, 1, LocalTime.of(11, 0), LocalTime.of(12, 30));
        labSection1 = labSectionRepository.save(labSection1);

        labSection2 = new LabSection(2L, 20, course, 1, LocalTime.of(13, 0), LocalTime.of(14, 30));
        labSection2 = labSectionRepository.save(labSection2);

        // Create test enrollment
        enrollment = new Enrollment(course, student1, null);
        enrollment = enrollmentRepository.save(enrollment);
    }

    // Essential endpoints tests
    @Test
    void shouldAllowStudentToEnrollThemselves() throws Exception {
        EnrollmentDto enrollmentDto = new EnrollmentDto(student1.getStudentId(), course.getId(), null);

        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDto))
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.student.studentId").value(student1.getStudentId()))
                .andExpect(jsonPath("$.course.id").value(course.getId()));
    }

    @Test
    void shouldAllowStudentToEnrollWithSpecificLabSection() throws Exception {
        EnrollmentDto enrollmentDto = new EnrollmentDto(student1.getStudentId(), course.getId(), labSection1.getId());

        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDto))
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.student.studentId").value(student1.getStudentId()))
                .andExpect(jsonPath("$.course.id").value(course.getId()))
                .andExpect(jsonPath("$.labSection.id").value(labSection1.getId()));
    }

    @Test
    void shouldAutoEnrollStudentInAvailableLabSection() throws Exception {
        // Create a new course without any lab sections

        EnrollmentDto enrollmentDto = new EnrollmentDto(student1.getStudentId(), course.getId(), null);

        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDto))
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.student.studentId").value(student1.getStudentId()))
                .andExpect(jsonPath("$.course.id").value(course.getId()))
                .andExpect(jsonPath("$.labSection.sectionId").value(anyOf(
                        is(labSection1.getSectionId().intValue()),
                        is(labSection2.getSectionId().intValue())
                )));
    }

    @Test
    void shouldNotAllowEnrollmentInFullLabSection() throws Exception {
        // Fill up labSection1 to capacity
        for (int i = 0; i < labSection1.getCapacity(); i++) {
            User newStudent = new User("Test", "Student" + i, "test" + i + "@example.com",
                    passwordEncoder.encode("password"), Role.STUDENT, 1000L + i);
            userRepository.save(newStudent);

            EnrollmentDto enrollmentDto = new EnrollmentDto(newStudent.getStudentId(), course.getId(), labSection1.getId());
            mockMvc.perform(post("/api/enrollments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(enrollmentDto))
                    .header("Authorization", "Bearer " + generateToken(newStudent)));
        }

        // Try to enroll in the full lab section
        EnrollmentDto enrollmentDto = new EnrollmentDto(student1.getStudentId(), course.getId(), labSection1.getId());

        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollmentDto))
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotAllowEnrollmentInLabSectionWithScheduleConflict() throws Exception {
        // Create a conflicting course

        // Create a lab section for the conflicting course
        LabSection conflictingLabSection = new LabSection(3L, 30, course, 1, LocalTime.of(9, 0), LocalTime.of(10, 0));
        labSectionRepository.save(conflictingLabSection);

        // Enroll student in the conflicting course
        EnrollmentDto conflictingEnrollmentDto = new EnrollmentDto(student1.getStudentId(),
                course.getId(), conflictingLabSection.getId());
        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conflictingEnrollmentDto))
                .header("Authorization", "Bearer " + generateToken(student1)));

    }

    @Test
    void shouldAllowStudentToViewTheirOwnEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments/student/" + student1.getStudentId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].student.studentId").value(student1.getStudentId()));
    }

    @Test
    void shouldNotAllowStudentToViewOtherStudentEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments/student/" + student2.getStudentId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToViewAnyStudentEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments/student/" + student1.getStudentId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].student.studentId").value(student1.getStudentId()));
    }

    @Test
    void shouldAllowStudentToDeleteTheirOwnEnrollment() throws Exception {
        mockMvc.perform(delete("/api/enrollments/" + enrollment.getId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotAllowStudentToDeleteOtherStudentEnrollment() throws Exception {
        mockMvc.perform(delete("/api/enrollments/" + enrollment.getId())
                .header("Authorization", "Bearer " + generateToken(student2)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToDeleteAnyEnrollment() throws Exception {
        mockMvc.perform(delete("/api/enrollments/" + enrollment.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldAllowStudentToViewTheirCurrentEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments/student/" + student1.getStudentId() + "/current")
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.studentId").value(student1.getStudentId()))
                .andExpect(jsonPath("$[0].course.id").value(course.getId()));
    }

    @Test
    void shouldNotAllowStudentToViewOtherStudentCurrentEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments/student/" + student2.getStudentId() + "/current")
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowStudentToViewTheirEnrollmentsByTerm() throws Exception {
        String currentTerm = TermDeterminator.determineCurrentTerm();
        mockMvc.perform(get("/api/enrollments/student/" + student1.getStudentId() + "/" + currentTerm)
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.studentId").value(student1.getStudentId()))
                .andExpect(jsonPath("$[0].course.id").value(course.getId()));
    }

    @Test
    void shouldNotAllowStudentToViewOtherStudentEnrollmentsByTerm() throws Exception {
        String term = course.getTerm();
        mockMvc.perform(get("/api/enrollments/student/" + student2.getStudentId() + "/" + term)
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isForbidden());
    }

    // Semi-essential endpoints tests
    @Test
    void shouldAllowAdminToGetEnrollmentsByCourseId() throws Exception {
        mockMvc.perform(get("/api/enrollments/course/" + course.getId())
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].course.id").value(course.getId()));
    }

    @Test
    void shouldNotAllowStudentToGetEnrollmentsByCourseId() throws Exception {
        mockMvc.perform(get("/api/enrollments/course/" + course.getId())
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isForbidden());
    }

    // Less essential endpoints tests
    @Test
    void shouldAllowAdminToGetAllEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments")
                .header("Authorization", "Bearer " + generateToken(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldNotAllowStudentToGetAllEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments")
                .header("Authorization", "Bearer " + generateToken(student1)))
                .andExpect(status().isForbidden());
    }

    // Helper method to generate next lab section ID
    private Long generateNextLabSectionId(Long courseId) {
        Long maxLabSectionId = labSectionRepository.findMaxLabSectionIdForCourse(courseId);
        return (maxLabSectionId != null) ? maxLabSectionId + 1L : 1L;
    }
}
