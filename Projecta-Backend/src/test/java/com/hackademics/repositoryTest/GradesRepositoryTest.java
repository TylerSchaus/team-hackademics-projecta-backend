package com.hackademics.repositoryTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.hackademics.model.Course;
import com.hackademics.model.Grade;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
public class GradesRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private User testAdmin;

    private Subject testSubject;

    private Course testCourse1;
    private Course testCourse2;

    private User testStudent1;
    private User testStudent2;

    private Grade grade1;
    private Grade grade2;

    @BeforeEach
    void setUp() {

        // Create a test admin 
        testAdmin = userRepository.save(new User("Test", "Admin", "test@test.com", "2317658909","password", Role.ADMIN, 500L));

        // Create a test subject
        testSubject = subjectRepository.save(new Subject("TestSubject", "TEST"));

        // Create test courses
        testCourse1 = courseRepository.save(new Course(testAdmin, testSubject, "TestCourse1", LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 50, "101", null, null, null));
        testCourse2 = courseRepository.save(new Course(testAdmin, testSubject, "TestCourse2", LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 50, "101", null, null, null));

        // Create test students
        testStudent1 = userRepository.save(new User("Test", "Student1", "student1@test.com", "2317658909","password", Role.STUDENT, 101L));
        testStudent2 = userRepository.save(new User("Test", "Student2", "student12@test.com", "2317658909","password", Role.STUDENT, 102L));

        // Create a grade using the constructor
        grade1 = new Grade(testStudent1, testCourse1, 90.5);
        grade2 = new Grade(testStudent2, testCourse2, 85.0);
    }

    @Test
    void testSaveGrade() {
        // Save the grade
        Grade savedGrade = gradeRepository.save(grade1);

        // Verify it has an auto-generated primary ID
        assertNotNull(savedGrade.getId(), "Saved grade should have an ID");
    }

    @Test
    void testFindGradeById() {
        // Save the grade
        Grade savedGrade = gradeRepository.save(grade1);

        // Find the grade by ID
        Optional<Grade> foundGrade = gradeRepository.findById(savedGrade.getId());

        // Verify the grade is found
        assertTrue(foundGrade.isPresent(), "Grade should be found by ID");
    }

    @Test
    void testDeleteGrade() {
        // Save the grade
        Grade savedGrade = gradeRepository.save(grade1);

        // Delete the grade
        gradeRepository.deleteById(savedGrade.getId());

        // Verify the grade is deleted
        Optional<Grade> deletedGrade = gradeRepository.findById(savedGrade.getId());
        assertFalse(deletedGrade.isPresent(), "Grade should be deleted from the database");
    }

    @Test
    void testFindByStudentId() {
        // Save grades
        gradeRepository.save(grade1);
        gradeRepository.save(grade2);

        // Find grades by studentId (e.g., student1)
        Long studentId = testStudent1.getStudentId(); // student1's ID

        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        // Verify that the correct grade is found for student1
        assertEquals(1, grades.size(), "Should find exactly 1 grade for studentId " + studentId);
        assertEquals(testCourse1.getCourseName(), grades.get(0).getCourseNameCopy());
        assertEquals(testStudent1.getStudentId(), grades.get(0).getStudentId());
    }

    @Test
    void testFindByCourseId() {
        // Save grades
        gradeRepository.save(grade1);
        gradeRepository.save(grade2);

        // Find grades by courseId (e.g., testCourse1)
        Long courseId = testCourse1.getId(); // testCourse1's ID

        List<Grade> grades = gradeRepository.findByCourseId(courseId);

        // Verify that the correct grade is found for testCourse1
        assertEquals(1, grades.size(), "Should find exactly 1 grade for courseId " + courseId);
        assertEquals(testCourse1.getCourseName(), grades.get(0).getCourseNameCopy());
        assertEquals(testStudent1.getStudentId(), grades.get(0).getStudentId());
    }

}
