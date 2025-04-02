package com.hackademics.repositoryTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based test profile
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    private User admin;
    private Subject testSubject;

    private Course course1; 
    private Course course2; 

    private Enrollment enrollment1;
    private Enrollment enrollment2;

    @BeforeEach
    void setUp() {

        admin = userRepository.save(new User("Test", "Admin", "admin@example.com", "2317658909","password", Role.ADMIN, 500L));

        // Create subjects
        testSubject = subjectRepository.save(new Subject("TestSubject", "TEST"));
        // Create courses
        course1 = new Course(admin, testSubject, "Mathematics 101", LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 50, "101", null, null, null);
        courseRepository.save(course1); 

        course2 = new Course(admin, testSubject, "Physics 101", LocalDateTime.now().plusMonths(6), LocalDateTime.now().plusMonths(12), 30, "101", null, null, null);
        courseRepository.save(course2);

        // Create students
        User student1 = userRepository.save(new User("Alice", "Example", "alice@example.com", "2317658909","password", Role.STUDENT, 1001L));
        User student2 = userRepository.save(new User("Bob", "Student", "bob@example.com", "2317658909","password", Role.STUDENT, 1002L));

        // Create enrollments
        enrollment1 = new Enrollment(course1, student1, null);
        enrollment2 = new Enrollment(course2, student2, null);
    }

    @Test
    void testSaveEnrollment() {
        // Save the enrollment
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment1);

        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedEnrollment.getId(), "Saved enrollment should have an ID");

        // Check that fields match
        Assertions.assertEquals(course1.getId(), savedEnrollment.getCourse().getId(), "Course ID should match");
        Assertions.assertEquals((Long)1001L, savedEnrollment.getStudent().getStudentId(), "Student ID should match");
    }

    @Test
    void testFindEnrollmentsByCourseId() {
        // Save both enrollments
        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);

        // Find enrollments by courseId
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course1.getId());

        Assertions.assertEquals(1, enrollments.size(), "Should find exactly 1 enrollment for courseId = 101");
        Assertions.assertEquals((Long)1001L, enrollments.get(0).getStudentId(), "Student ID should match");
    }

    @Test
    void testFindEnrollmentsByStudentId() {
        // Save both enrollments
        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);

        // Find enrollments by studentId
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(1001L);

        Assertions.assertEquals(1, enrollments.size(), "Should find exactly 1 enrollment for studentId = 1001");
        Assertions.assertEquals(course1.getId(), enrollments.get(0).getCourse().getId(), "Course ID should match");
    }

    @Test
    void testGetEnrollmentById() {
        // Save an enrollment
        Enrollment saved = enrollmentRepository.save(enrollment1);
        Long id = saved.getId();

        // Retrieve by that ID
        Optional<Enrollment> found = enrollmentRepository.findById(id);

        Assertions.assertTrue(found.isPresent(), "Enrollment should exist by that primary ID");
        Assertions.assertEquals(course1.getId(), found.get().getCourse().getId(), "Course ID should match");
    }

    @Test
    void testUpdateEnrollment() {
        // Save the enrollment initially
        Enrollment saved = enrollmentRepository.save(enrollment1);
        Long id = saved.getId();

        // Modify some fields (e.g., change the course)
        Course newCourse = new Course(admin, testSubject, "Chemistry 101", LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 40, "101", null, null, null);
        saved.setCourse(newCourse);
        enrollmentRepository.save(saved);

        // Fetch again
        Enrollment updated = enrollmentRepository.findById(id).orElseThrow();
        Assertions.assertEquals("Chemistry 101", updated.getCourse().getCourseName(), "Updated course name should match");
    }

    @Test
    void testDeleteEnrollment() {
        Enrollment saved = enrollmentRepository.save(enrollment1);
        Long id = saved.getId();

        enrollmentRepository.deleteById(id);

        Optional<Enrollment> afterDelete = enrollmentRepository.findById(id);
        Assertions.assertTrue(afterDelete.isEmpty(), "Enrollment should be deleted from the database");
    }

    @Test
    void testFindEnrollmentByTermAndStudent(){
        enrollmentRepository.save(enrollment1);

        String currentTerm = course1.getTerm();

        List<Enrollment> enrollments = enrollmentRepository.findByTermAndStudentId(currentTerm, 1001L);

        Assertions.assertEquals(1, enrollments.size(), "Should find exactly 1 enrollment for the current term and student"); // Courses 6 months apart, never will belong to same term. 
    }
}
