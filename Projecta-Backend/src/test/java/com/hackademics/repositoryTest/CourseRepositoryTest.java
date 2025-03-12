package com.hackademics.repositoryTest;


import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.SubjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based application-test.properties
class CourseRepositoryTest {


    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private Course course1;
    private Course course2;


    @BeforeEach
    void setUp() {
        // Create an admin user

        userRepository.deleteAll();
        subjectRepository.deleteAll();
        courseRepository.deleteAll(); 

        User admin = new User();
        admin.setAdminId(5001L);
        admin.setFirstName("Bob");
        admin.setLastName("Admin");
        admin.setEmail("bob@example.com");
        admin.setPassword("password");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Create a subject
        Subject subject1 = new Subject();
        subject1.setSubjectName("Mathematics");
        subjectRepository.save(subject1);


        Subject subject2 = new Subject();
        subject2.setSubjectName("Physics");
        subjectRepository.save(subject2);


        // Create a course with admin and subject
        course1 = new Course();
        course1.setAdmin(admin);
        course1.setSubject(subject1);
        course1.setCourseName("Mathematics 101");
        course1.setStartDate(LocalDateTime.now());
        course1.setEndDate(LocalDateTime.now().plusMonths(6));
        course1.setEnrollLimit(50);


        // Create another course with the same admin and a different subject
        course2 = new Course();
        course2.setAdmin(admin);
        course2.setSubject(subject2);
        course2.setCourseName("Physics 101");
        course2.setStartDate(LocalDateTime.now());
        course2.setEndDate(LocalDateTime.now().plusMonths(6));
        course2.setEnrollLimit(30);
    }


    @Test
    void testSaveCourse() {
        // Save the course
        Course savedCourse = courseRepository.save(course1);


        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedCourse.getId(), "Saved course should have an ID");
        // Check that fields match
        Assertions.assertEquals("Mathematics 101", savedCourse.getCourseName());
        Assertions.assertEquals(5001L, savedCourse.getAdmin().getId());
        Assertions.assertEquals(101L, savedCourse.getSubject().getId());
    }


    @Test
    void testFindCoursesByAdminId() {
        // Save both courses
        courseRepository.save(course1);
        courseRepository.save(course2);


        // Find courses by adminId
        List<Course> courses = courseRepository.findByAdminId(5001L);
        Assertions.assertEquals(2, courses.size(), "Should find exactly 2 courses for adminId=5001");
        Assertions.assertEquals("Mathematics 101", courses.get(0).getCourseName());
        Assertions.assertEquals("Physics 101", courses.get(1).getCourseName());
    }


    @Test
    void testFindCoursesBySubjectName() {
        // Save both courses
        courseRepository.save(course1);
        courseRepository.save(course2);


        // Find courses by subjectId
        List<Course> courses = courseRepository.findBySubjectName("Mathematics");
        Assertions.assertEquals(1, courses.size(), "Should find exactly 1 course for subjectId=101");
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
}

