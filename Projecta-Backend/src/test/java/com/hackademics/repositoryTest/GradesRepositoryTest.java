package com.hackademics.repositoryTest;

import com.hackademics.Model.Grade;
import com.hackademics.Model.User;
import com.hackademics.Model.Course;
import com.hackademics.repository.GradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.time.LocalDateTime;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class GradesRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GradeRepository gradesRepository;

    @Test
    public void testFindByStudentId() {
        // Arrange
        User student = new User();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        student.setPassword("password");
        entityManager.persist(student);

        Course course = new Course();
        course.setAdminId(1L);
        course.setSubjectId(1L);
        course.setStartDate(LocalDateTime.now());
        course.setEndDate(LocalDateTime.now().plusDays(30));
        course.setEnrollLimit(50);
        entityManager.persist(course);

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setCourse(course);
        grade.setGrade(85.5);
        entityManager.persist(grade);

        // Act
        List<Grade> foundGrades = gradesRepository.findByStudentId(student.getId());

        // Assert
        assertThat(foundGrades).hasSize(1);
        assertThat(foundGrades.get(0).getGrade()).isEqualTo(85.5);
    }
}