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

@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based test profile
public class WaitlistEnrollmentRepositoryTest {

    @Autowired
    private WaitlistEnrollmentRepository waitlistEnrollmentRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private WaitlistEnrollment enrollment1;
    private WaitlistEnrollment enrollment2;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {

        User admin = userRepository.save(new User("Test", "Admin", "admin@example.com", "2317658909","password", Role.ADMIN, 500L));

        // Create subjects
        Subject testSubject = subjectRepository.save(new Subject("TestSubject", "TEST"));

        // Create courses
        course1 = new Course(admin, testSubject, "Mathematics 101", LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 50, "MATH101", null, null, null);
        courseRepository.save(course1);

        course2 = new Course(admin, testSubject, "Physics 101", LocalDateTime.now().plusMonths(6), LocalDateTime.now().plusMonths(12), 30, "PHYS101", null, null, null);
        courseRepository.save(course2);

        // Create students
        User student1 = userRepository.save(new User("Alice", "Example", "alice@example.com", "2317658909","password", Role.STUDENT, 1001L));
        User student2 = userRepository.save(new User("Bob", "Student", "bob@example.com", "2317658909","password", Role.STUDENT, 1002L));

        // Create waitlists
        Waitlist waitlist1 = waitlistRepository.save(new Waitlist(course1, 10));
        Waitlist waitlist2 = waitlistRepository.save(new Waitlist(course2, 15));

        // Create waitlist enrollments
        enrollment1 = waitlistEnrollmentRepository.save(new WaitlistEnrollment(1, waitlist1, student1));
        enrollment2 = waitlistEnrollmentRepository.save(new WaitlistEnrollment(1, waitlist2, student2));
    }

    @Test
    void testSaveWaitlistEnrollment() {
        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(enrollment1.getId(), "Saved waitlist enrollment should have an ID");

        // Check that fields match
        Assertions.assertEquals(1, enrollment1.getWaitlistPosition(), "Waitlist position should match");
        Assertions.assertEquals(course1.getId(), enrollment1.getWaitlist().getCourse().getId(), "Course ID should match");
        Assertions.assertEquals(1001L, enrollment1.getStudent().getStudentId(), "Student ID should match");
    }

    @Test
    void testFindByWaitlistId() {
        // Find waitlist enrollments by waitlistId
        List<WaitlistEnrollment> enrollments = waitlistEnrollmentRepository.findByWaitlistId(enrollment1.getWaitlist().getId());

        Assertions.assertEquals(1, enrollments.size(), "Should find exactly 1 enrollment for the waitlist ID");
        Assertions.assertEquals(1001L, enrollments.get(0).getStudent().getStudentId(), "Student ID should match");
    }

    @Test
    void testFindByStudentId() {
        // Find waitlist enrollments by studentId
        List<WaitlistEnrollment> enrollments = waitlistEnrollmentRepository.findByStudentId(1001L);

        Assertions.assertEquals(1, enrollments.size(), "Should find exactly 1 enrollment for the student ID");
        Assertions.assertEquals(course1.getId(), enrollments.get(0).getWaitlist().getCourse().getId(), "Course ID should match");
    }

    @Test
    void testFindById() {
        // Retrieve by ID
        Optional<WaitlistEnrollment> found = waitlistEnrollmentRepository.findById(enrollment1.getId());

        Assertions.assertTrue(found.isPresent(), "Waitlist enrollment should exist by that primary ID");
        Assertions.assertEquals(course1.getId(), found.get().getWaitlist().getCourse().getId(), "Course ID should match");
    }

    @Test
    void testUpdateWaitlistEnrollment() {
        // Modify some fields (e.g., change the waitlist position)
        enrollment1.setWaitlistPosition(2);
        waitlistEnrollmentRepository.save(enrollment1);

        // Fetch again
        WaitlistEnrollment updated = waitlistEnrollmentRepository.findById(enrollment1.getId()).orElseThrow();
        Assertions.assertEquals(2, updated.getWaitlistPosition(), "Updated waitlist position should match");
    }

    @Test
    void testDeleteWaitlistEnrollment() {
        // Delete the waitlist enrollment
        waitlistEnrollmentRepository.deleteById(enrollment1.getId());

        // Verify the enrollment is deleted
        Optional<WaitlistEnrollment> afterDelete = waitlistEnrollmentRepository.findById(enrollment1.getId());
        Assertions.assertTrue(afterDelete.isEmpty(), "Waitlist enrollment should be deleted from the database");
    }

    @Test
    void testFindAll() {
        // Find all waitlist enrollments
        List<WaitlistEnrollment> allEnrollments = waitlistEnrollmentRepository.findAll();

        Assertions.assertEquals(2, allEnrollments.size(), "Should find exactly 2 waitlist enrollments");
    }
}
