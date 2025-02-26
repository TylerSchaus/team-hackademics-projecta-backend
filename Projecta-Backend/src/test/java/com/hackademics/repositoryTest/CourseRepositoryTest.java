package com.hackademics.repositoryTest;

import com.hackademics.Model.Course;
import com.hackademics.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Enables JPA testing with an in-memory database
public class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Manages persistence context for tests

    @Autowired
    private CourseRepository courseRepository; // Repository to be tested

    @Test
    public void testFindByAdminId() {
        // Arrange
        Course course = new Course();
        course.setAdminId(1L);
        course.setSubjectId(1L);
        course.setStartDate(LocalDateTime.now());
        course.setEndDate(LocalDateTime.now().plusDays(30));
        course.setEnrollLimit(50);
        entityManager.persist(course); // Persist the course to the in-memory database

        // Act
        List<Course> foundCourses = courseRepository.findByAdminId(1L);

        // Assert
        assertThat(foundCourses).hasSize(1); // Check that one course is found
        assertThat(foundCourses.get(0).isEqualTo(course); // Verify the course matches
    }

    @Test
    public void testFindBySubjectId() {
        // Arrange
        Course course = new Course();
        course.setAdminId(1L);
        course.setSubjectId(1L);
        course.setStartDate(LocalDateTime.now());
        course.setEndDate(LocalDateTime.now().plusDays(30));
        course.setEnrollLimit(50);
        entityManager.persist(course); // Persist the course to the in-memory database

        // Act
        List<Course> foundCourses = courseRepository.findBySubjectId(1L);

        // Assert
        assertThat(foundCourses).hasSize(1); // Check that one course is found
        assertThat(foundCourses.get(0)).isEqualTo(course); // Verify the course matches
    }

    @Test
    public void testSaveCourse() {
        // Arrange
        Course course = new Course();
        course.setAdminId(1L);
        course.setSubjectId(1L);
        course.setStartDate(LocalDateTime.now());
        course.setEndDate(LocalDateTime.now().plusDays(30));
        course.setEnrollLimit(50);

        // Act
        Course savedCourse = courseRepository.save(course);

        // Assert
        assertThat(savedCourse.getCourseId()).isNotNull(); // Verify the course ID is generated
        assertThat(savedCourse.getEnrollLimit()).isEqualTo(50); // Verify the enroll limit is correct
    }

    @Test
    public void testDeleteCourse() {
        // Arrange
        Course course = new Course();
        course.setAdminId(1L);
        course.setSubjectId(1L);
        course.setStartDate(LocalDateTime.now());
        course.setEndDate(LocalDateTime.now().plusDays(30));
        course.setEnrollLimit(50);
        entityManager.persist(course); // Persist the course to the in-memory database

        // Act
        courseRepository.delete(course);

        // Assert
        Optional<Course> deletedCourse = courseRepository.findById(course.getCourseId());
        assertThat(deletedCourse).isEmpty(); // Verify the course is deleted
    }
}