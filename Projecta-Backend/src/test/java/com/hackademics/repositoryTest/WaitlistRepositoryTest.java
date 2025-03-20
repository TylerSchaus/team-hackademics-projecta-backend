package com.hackademics.repositoryTest;

import java.time.LocalDateTime;
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
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistRepository;

@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based test profile
class WaitlistRepositoryTest {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository; 

    private User testAdmin;
    private User testStudent1;
    private User testStudent2;

    private Course testCourse1;
    private Course testCourse2;

    private Subject testSubject; 

    private Waitlist waitlist1;
    private Waitlist waitlist2;

    @BeforeEach
    void setUp() {
         // Create a test admin 
        testAdmin = userRepository.save(new User("Test", "Admin", "test@test.com", "testpassword", Role.ADMIN, 500L));

        // Create a test subject
        testSubject = subjectRepository.save(new Subject("TestSubject", "TEST"));

        // Create test courses
        testCourse1 = courseRepository.save(new Course(testAdmin, testSubject, "TestCourse1", LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 50, "101", null, null, null));
        testCourse2 = courseRepository.save(new Course(testAdmin, testSubject, "TestCourse2", LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 50, "101", null, null, null));

        // Create waitlist entries
        waitlist1 = new Waitlist(testCourse1, 10);

        waitlist2 = new Waitlist(testCourse2, 15);
    }

    @Test
    void testSaveWaitlist() {
        // Save the waitlist entry
        Waitlist savedWaitlist = waitlistRepository.save(waitlist1);
        Long id = savedWaitlist.getCourse().getId();

        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedWaitlist.getId(), "Saved waitlist entry should have an ID");

        // Check that fields match
        Assertions.assertEquals(id, savedWaitlist.getCourse().getId(), "Course ID should match");
        Assertions.assertEquals(10, savedWaitlist.getWaitlistLimit(), "Waitlist limit should match");
        Assertions.assertEquals(0, savedWaitlist.getWaitlistEnrollments().size(), "Waitlist enrollments size should match");
    }

    @Test
    void testFindWaitlistsByCourseId() {
        // Save both waitlist entries
        waitlistRepository.save(waitlist1);
        waitlistRepository.save(waitlist2);

        Long courseId = waitlist1.getCourse().getId();

        // Find waitlist entries by courseId
        Waitlist waitlist = waitlistRepository.findByCourseId(courseId);

        Assertions.assertEquals(courseId, waitlist.getCourse().getId(), "Course id of returned waitlist should be equal to: "+courseId); 
    }

    @Test
    void testGetWaitlistById() {
        // Save a waitlist entry
        Waitlist saved = waitlistRepository.save(waitlist1);
        Long id = saved.getId();
        Long courseId = saved.getCourse().getId(); 

        // Retrieve by that ID
        Optional<Waitlist> found = waitlistRepository.findById(id);

        Assertions.assertTrue(found.isPresent(), "Waitlist entry should exist by that primary ID");
        Assertions.assertEquals(courseId, found.get().getCourse().getId(), "Course ID should match");
    }

    @Test
    void testUpdateWaitlist() {
        // Save the waitlist entry initially
        Waitlist saved = waitlistRepository.save(waitlist1);
        Long id = saved.getId();

        // Modify some fields (e.g., change the waitlist limit)
        saved.setWaitlistLimit(20);
        waitlistRepository.save(saved);

        // Fetch again
        Waitlist updated = waitlistRepository.findById(id).orElseThrow();
        Assertions.assertEquals(20, updated.getWaitlistLimit(), "Updated waitlist limit should match");
    }

    @Test
    void testDeleteWaitlist() {
        Waitlist saved = waitlistRepository.save(waitlist1);
        Long id = saved.getId();

        waitlistRepository.deleteById(id);

        Optional<Waitlist> afterDelete = waitlistRepository.findById(id);
        Assertions.assertTrue(afterDelete.isEmpty(), "Waitlist entry should be deleted from the database");
    }
}
