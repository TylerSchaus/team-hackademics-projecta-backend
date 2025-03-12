package com.hackademics.repositoryTest;


import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.EnrollmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;
import java.util.Optional; // Correct import for Optional


@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based application-test.properties
class EnrollmentRepositoryTest {


    @Autowired
    private EnrollmentRepository enrollmentRepository;


    private Enrollment enrollment1;
    private Enrollment enrollment2;


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


        // Create enrollments
        enrollment1 = new Enrollment();
        enrollment1.setCourse(course1);
        enrollment1.setStudent(student1);


        enrollment2 = new Enrollment();
        enrollment2.setCourse(course2);
        enrollment2.setStudent(student2);
    }


    @Test
    void testSaveEnrollment() {
        // Save the enrollment
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment1);


        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedEnrollment.getId(), "Saved enrollment should have an ID");
        // Check that fields match
        Assertions.assertEquals(101L, savedEnrollment.getCourse().getId());
        Assertions.assertEquals(1001L, savedEnrollment.getStudent().getId());
    }


    @Test
    void testFindEnrollmentsByCourseId() {
        // Save both enrollments
        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);


        // Find enrollments by courseId
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(101L);
        Assertions.assertEquals(1, enrollments.size(), "Should find exactly 1 enrollment for courseId=101");
        Assertions.assertEquals(1001L, enrollments.get(0).getStudent().getId());
    }


    @Test
    void testFindEnrollmentsByStudentId() {
        // Save both enrollments
        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);


        // Find enrollments by studentId
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(1001L);
        Assertions.assertEquals(1, enrollments.size(), "Should find exactly 1 enrollment for studentId=1001");
        Assertions.assertEquals(101L, enrollments.get(0).getCourse().getId());
    }


    @Test
    void testGetEnrollmentById() {
        // Save an enrollment
        Enrollment saved = enrollmentRepository.save(enrollment1);
        Long id = saved.getId();


        // Retrieve by that ID
        Optional<Enrollment> found = enrollmentRepository.findById(id);
        Assertions.assertTrue(found.isPresent(), "Enrollment should exist by that primary ID");
        Assertions.assertEquals(101L, found.get().getCourse().getId());
    }


    @Test
    void testUpdateEnrollment() {
        // Save the enrollment initially
        Enrollment saved = enrollmentRepository.save(enrollment1);
        Long id = saved.getId();


        // Modify some fields (e.g., change the course)
        Course newCourse = new Course();
        newCourse.setId(103L);
        newCourse.setCourseName("Chemistry 101");
        saved.setCourse(newCourse);
        enrollmentRepository.save(saved);


        // Fetch again
        Enrollment updated = enrollmentRepository.findById(id).orElseThrow();
        Assertions.assertEquals(103L, updated.getCourse().getId());
    }


    @Test
    void testDeleteEnrollment() {
        Enrollment saved = enrollmentRepository.save(enrollment1);
        Long id = saved.getId();


        enrollmentRepository.deleteById(id);


        Optional<Enrollment> afterDelete = enrollmentRepository.findById(id);
        Assertions.assertTrue(afterDelete.isEmpty(), "Enrollment should be deleted from the database");
    }
}

