package com.hackademics.repositoryTest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.hackademics.model.Course;
import com.hackademics.model.LabSection;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based test profile
public class LabSectionRepositoryTest {

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    private LabSection labSection1;
    private LabSection labSection2;

private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {

        User admin = userRepository.save(new User("Test", "Admin", "admin@example.com", "password", Role.ADMIN, 500L));

        // Create subjects
        Subject testSubject = subjectRepository.save(new Subject("TestSubject", "TEST"));

        // Create courses
        course1 = new Course(admin, testSubject, "Mathematics 101", LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 50, "MATH101", null, null, null);
        courseRepository.save(course1);

        course2 = new Course(admin, testSubject, "Physics 101", LocalDateTime.now().plusMonths(6), LocalDateTime.now().plusMonths(12), 30, "PHYS101", null, null, null);
        courseRepository.save(course2);

        // Create lab sections
        labSection1 = new LabSection(1L, 20, course1, 3, LocalTime.of(9, 0), LocalTime.of(11, 0));
        labSection2 = new LabSection(2L, 25, course2, 5, LocalTime.of(14, 0), LocalTime.of(16, 0));
        labSectionRepository.save(labSection1);
        labSectionRepository.save(labSection2);
    }

    @Test
    void testSaveLabSection() {
        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(labSection1.getId(), "Saved lab section should have an ID");

        // Check that fields match
        Assertions.assertEquals(1L, labSection1.getSectionId(), "Section ID should match");
        Assertions.assertEquals(course1.getId(), labSection1.getCourse().getId(), "Course ID should match");
        Assertions.assertEquals(20, labSection1.getCapacity(), "Capacity should match");
    }

    @Test
    void testFindByCourseId() {
        // Find lab sections by courseId
        List<LabSection> labSections = labSectionRepository.findByCourseId(course1.getId());

        Assertions.assertEquals(1, labSections.size(), "Should find exactly 1 lab section for courseId=101");
        Assertions.assertEquals(1L, labSections.get(0).getSectionId(), "Section ID should match");
    }

    @Test
    void testFindMaxLabSectionIdForCourse() {
        // Find the max lab section ID for a course
        Long maxSectionId = labSectionRepository.findMaxLabSectionIdForCourse(course1.getId());

        Assertions.assertEquals(1L, maxSectionId, "Max lab section ID should match");
    }

    @Test
    void testFindById() {
        // Retrieve by ID
        Optional<LabSection> found = labSectionRepository.findById(labSection1.getId());

        Assertions.assertTrue(found.isPresent(), "Lab section should exist by that primary ID");
        Assertions.assertEquals(course1.getId(), found.get().getCourse().getId(), "Course ID should match");
    }

    @Test
    void testUpdateLabSection() {
        // Modify some fields (e.g., change the capacity)
        labSection1.setCapacity(30);
        labSectionRepository.save(labSection1);

        // Fetch again
        LabSection updated = labSectionRepository.findById(labSection1.getId()).orElseThrow();
        Assertions.assertEquals(30, updated.getCapacity(), "Updated capacity should match");
    }

    @Test
    void testDeleteLabSection() {
        // Delete the lab section
        labSectionRepository.deleteById(labSection1.getId());

        // Verify the lab section is deleted
        Optional<LabSection> afterDelete = labSectionRepository.findById(labSection1.getId());
        Assertions.assertTrue(afterDelete.isEmpty(), "Lab section should be deleted from the database");
    }

    @Test
    void testFindAll() {
        // Find all lab sections
        List<LabSection> allLabSections = labSectionRepository.findAll();

        Assertions.assertEquals(2, allLabSections.size(), "Should find exactly 2 lab sections");
    }
}
