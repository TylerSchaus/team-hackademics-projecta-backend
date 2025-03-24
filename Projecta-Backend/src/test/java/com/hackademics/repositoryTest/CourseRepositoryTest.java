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
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based test profile
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        // Create an admin user
        User admin = userRepository.save(new User("Bob", "Admin", "bob@example.com", "password", Role.ADMIN, 500L));

        // Create subjects
        Subject subject1 = subjectRepository.save(new Subject("Mathematics", "MATH"));
        Subject subject2 = subjectRepository.save(new Subject("Physics", "PHYS"));

        // Create courses with constructors
        course1 = new Course(admin, subject1, "Mathematics 101", LocalDateTime.now(),
                LocalDateTime.now().plusMonths(6), 50, "MATH101", null, null, null);

        course2 = new Course(admin, subject2, "Physics 101", LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(6), 30, "101", null, null, null);
    }

    @Test
    void testSaveCourse() {
        // Save the course

        Course savedCourse = courseRepository.save(course1);

        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedCourse.getId(), "Saved course should have an ID");

        // Check that fields match
        Assertions.assertEquals("Mathematics 101", savedCourse.getCourseName());
        Assertions.assertEquals((Long) 500L, savedCourse.getAdminId());
        Assertions.assertEquals("MATH", savedCourse.getSubject().getSubjectTag());
    }

    @Test
    void testFindCoursesByAdminId() {
        // Save both courses
        courseRepository.save(course1);
        courseRepository.save(course2);

        List<Course> courses = courseRepository.findByAdminId(500L);

        courses.forEach(course -> System.out.println("Found Course: " + course.getCourseName()));

        Assertions.assertEquals(2, courses.size(), "Should find exactly 2 courses for adminId = 500");
        Assertions.assertEquals("Mathematics 101", courses.get(0).getCourseName());
        Assertions.assertEquals("Physics 101", courses.get(1).getCourseName());
    }

    @Test
    void testFindCoursesBySubjectId() {
        // Save both courses
        courseRepository.save(course1);
        courseRepository.save(course2);
        // Find courses by subjectId

        Long subjectId = course1.getSubject().getId();

        List<Course> courses = courseRepository.findBySubjectId(subjectId);
        Assertions.assertEquals(1, courses.size(), "Should find exactly 1 course");
        Assertions.assertEquals("Mathematics 101", courses.get(0).getCourseName());
    }

    @Test
    void testGetCourseById() {
        // Save a course
        Course saved = courseRepository.save(course1);
        Long id = saved.getId();

        // Retrieve by that ID
        Optional<Course> found = courseRepository.findById(id);
        Assertions.assertTrue(found.isPresent(), "Course should exist by that primary ID");
        Assertions.assertEquals("Mathematics 101", found.get().getCourseName());
    }

    @Test
    void testUpdateCourse() {
        // Save the course initially
        Course saved = courseRepository.save(course1);
        Long id = saved.getId();

        // Modify some fields
        saved.setCourseName("Advanced Mathematics");
        courseRepository.save(saved);

        // Fetch again
        Course updated = courseRepository.findById(id).orElseThrow();
        Assertions.assertEquals("Advanced Mathematics", updated.getCourseName());
    }

    @Test
    void testDeleteCourse() {
        Course saved = courseRepository.save(course1);
        Long id = saved.getId();

        courseRepository.deleteById(id);

        Optional<Course> afterDelete = courseRepository.findById(id);
        Assertions.assertTrue(afterDelete.isEmpty(), "Course should be deleted from the database");
    }

    @Test
    void testFindByStartDateAfter() {
        // Save both courses
        courseRepository.save(course1);
        courseRepository.save(course2);

        // Get the current time for comparison
        LocalDateTime now = LocalDateTime.now();

        // Find courses with start date after now
        List<Course> courses = courseRepository.findByStartDateAfter(now);

        // Assert that the course with the start date after the current time is returned
        Assertions.assertEquals(1, courses.size(), "Should find exactly 1 course with a start date after now");
        Assertions.assertEquals("Physics 101", courses.get(0).getCourseName());
    }

    @Test
    void testFindByStartDateBeforeAndEndDateAfter() {
        // Save both courses
        courseRepository.save(course1);
        courseRepository.save(course2);

        // Get the current time for comparison
        LocalDateTime now = LocalDateTime.now().plusDays(1); // startDate should be before this

        // Find courses with start date before now1 and end date after now2
        List<Course> courses = courseRepository.findByStartDateBeforeAndEndDateAfter(now, now);

        // Assert that only the correct course is returned (e.g., "Mathematics 101")
        Assertions.assertEquals(1, courses.size(), "Should find exactly 1 course with start date before now1 and end date after now2");
        Assertions.assertEquals("Mathematics 101", courses.get(0).getCourseName());
    }

}
