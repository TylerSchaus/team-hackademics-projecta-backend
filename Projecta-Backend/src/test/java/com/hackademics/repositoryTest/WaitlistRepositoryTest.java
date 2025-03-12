package com.hackademics.repositoryTest;


import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.WaitlistRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;
import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based application-test.properties
class WaitlistRepositoryTest {


    @Autowired
    private WaitlistRepository waitlistRepository;


    private Waitlist waitlist1;
    private Waitlist waitlist2;


    @BeforeEach
    void setUp() {
        // Create a course
        Course course1 = new Course();
        course1.setId(101L);
        course1.setCourseName("Mathematics 101");


        Course course2 = new Course();
        course2.setId(102L);
        course2.setCourseName("Physics 101");


        // Create a student
        User student1 = new User();
        student1.setId(1001L);
        student1.setFirstName("Alice");
        student1.setLastName("Example");
        student1.setEmail("alice@example.com");
        student1.setPassword("password");
        student1.setRole(Role.STUDENT);


        User student2 = new User();
        student2.setId(1002L);
        student2.setFirstName("Bob");
        student2.setLastName("Student");
        student2.setEmail("bob@example.com");
        student2.setPassword("password");
        student2.setRole(Role.STUDENT);


        // Create waitlist entries
        waitlist1 = new Waitlist();
        waitlist1.setCourse(course1);
        waitlist1.setWaitlistLimit(10);


        WaitlistEnrollment enrollment1 = new WaitlistEnrollment();
        enrollment1.setStudent(student1);
        enrollment1.setWaitlist(waitlist1);
        waitlist1.getWaitlistEnrollments().add(enrollment1);


        waitlist2 = new Waitlist();
        waitlist2.setCourse(course2);
        waitlist2.setWaitlistLimit(15);


        WaitlistEnrollment enrollment2 = new WaitlistEnrollment();
        enrollment2.setStudent(student2);
        enrollment2.setWaitlist(waitlist2);
        waitlist2.getWaitlistEnrollments().add(enrollment2);
    }


    @Test
    void testSaveWaitlist() {
        // Save the waitlist entry
        Waitlist savedWaitlist = waitlistRepository.save(waitlist1);


        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedWaitlist.getId(), "Saved waitlist entry should have an ID");
        // Check that fields match
        Assertions.assertEquals(101L, savedWaitlist.getCourse().getId());
        Assertions.assertEquals(10, savedWaitlist.getWaitlistLimit());
        Assertions.assertEquals(1, savedWaitlist.getWaitlistEnrollments().size());
    }


    @Test
    void testFindWaitlistsByCourseId() {
        // Save both waitlist entries
        waitlistRepository.save(waitlist1);
        waitlistRepository.save(waitlist2);


        // Find waitlist entries by courseId
        List<Waitlist> waitlists = waitlistRepository.findByCourseId(101L);
        Assertions.assertEquals(1, waitlists.size(), "Should find exactly 1 waitlist entry for courseId=101");
        Assertions.assertEquals(101L, waitlists.get(0).getCourse().getId());
    }


    @Test
    void testGetWaitlistById() {
        // Save a waitlist entry
        Waitlist saved = waitlistRepository.save(waitlist1);
        Long id = saved.getId();


        // Retrieve by that ID
        Optional<Waitlist> found = waitlistRepository.findById(id);
        Assertions.assertTrue(found.isPresent(), "Waitlist entry should exist by that primary ID");
        Assertions.assertEquals(101L, found.get().getCourse().getId());
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
        Assertions.assertEquals(20, updated.getWaitlistLimit());
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


