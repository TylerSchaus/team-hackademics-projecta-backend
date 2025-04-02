package com.hackademics.repositoryTest;

import java.time.LocalDateTime;
import java.util.List;

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
import com.hackademics.model.WaitlistRequest;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.repository.WaitlistRequestRepository;

@DataJpaTest
@ActiveProfiles("test")
class WaitlistRequestRepositoryTest {

    @Autowired
    private WaitlistRequestRepository waitlistRequestRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private WaitlistRequest request1, request2;
    private Waitlist waitlist1, waitlist2;
    private User student1, student2;
    private Course course1, course2;
    private Subject subject;
    private User admin;

    @BeforeEach
    void setUp() {
        // Create and save an admin user
        admin = new User("Admin", "User", "admin@example.com", "1234567890", "password", Role.ADMIN, 1000L);
        admin = userRepository.save(admin);

        // Create and save a subject
        subject = new Subject("Test Subject", "TEST");
        subject = subjectRepository.save(subject);

        // Create and save courses
        course1 = new Course(admin, subject, "Test Course 1", LocalDateTime.now(), 
            LocalDateTime.now().plusMonths(6), 10, "TEST101", null, null, null);
        course2 = new Course(admin, subject, "Test Course 2", LocalDateTime.now(), 
            LocalDateTime.now().plusMonths(6), 10, "TEST102", null, null, null);
        course1 = courseRepository.save(course1);
        course2 = courseRepository.save(course2);

        // Create and save waitlists
        waitlist1 = new Waitlist(course1, 10);
        waitlist2 = new Waitlist(course2, 10);
        waitlist1 = waitlistRepository.save(waitlist1);
        waitlist2 = waitlistRepository.save(waitlist2);

        // Create and save students
        student1 = new User("Student", "One", "student1@example.com", "1234567890", "password", Role.STUDENT, 1001L);
        student2 = new User("Student", "Two", "student2@example.com", "1234567891", "password", Role.STUDENT, 1002L);
        student1 = userRepository.save(student1);
        student2 = userRepository.save(student2);

        // Create and save waitlist requests
        request1 = new WaitlistRequest(waitlist1, student1);
        request2 = new WaitlistRequest(waitlist2, student2);
        request1 = waitlistRequestRepository.save(request1);
        request2 = waitlistRequestRepository.save(request2);
    }

    @Test
    void testSaveWaitlistRequest() {
        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(request1.getId(), "Saved waitlist request should have an ID");

        // Check that fields match
        Assertions.assertEquals(waitlist1.getId(), request1.getWaitlist().getId(), "Waitlist ID should match");
        Assertions.assertEquals(student1.getStudentId(), request1.getStudent().getStudentId(), "Student ID should match");
        Assertions.assertEquals(student1.getStudentId(), request1.getStudentId(), "Student ID in request should match");
    }

    @Test
    void testFindAllWaitlistRequests() {
        // Find all waitlist requests
        List<WaitlistRequest> requests = waitlistRequestRepository.findAll();

        Assertions.assertEquals(2, requests.size(), "Should find exactly 2 waitlist requests");
        Assertions.assertTrue(requests.contains(request1), "Should contain first request");
        Assertions.assertTrue(requests.contains(request2), "Should contain second request");
    }

    @Test
    void testDeleteWaitlistRequest() {
        // Delete a request
        waitlistRequestRepository.deleteById(request1.getId());

        // Verify it's deleted
        List<WaitlistRequest> remainingRequests = waitlistRequestRepository.findAll();
        Assertions.assertEquals(1, remainingRequests.size(), "Should have exactly 1 request remaining");
        Assertions.assertFalse(remainingRequests.contains(request1), "Deleted request should not be in the list");
        Assertions.assertTrue(remainingRequests.contains(request2), "Other request should still exist");
    }
}
