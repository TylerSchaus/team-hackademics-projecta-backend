package com.hackademics.repositoryTest;


import com.hackademics.model.Grade;
import com.hackademics.repository.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class GradesRepositoryTest {


    @Autowired
    private GradeRepository gradeRepository;


    private Grade grade;


    @BeforeEach
    void setUp() {
        grade = new Grade();
        grade.setGrade(90.5);
    }


    @Test
    void testSaveGrade() {
        Grade savedGrade = gradeRepository.save(grade);
        assertNotNull(savedGrade.getId());
    }


    @Test
    void testFindGradeById() {
        Grade savedGrade = gradeRepository.save(grade);
        Optional<Grade> foundGrade = gradeRepository.findById(savedGrade.getId());
        assertTrue(foundGrade.isPresent());
    }


    @Test
    void testDeleteGrade() {
        Grade savedGrade = gradeRepository.save(grade);
        gradeRepository.deleteById(savedGrade.getId());
        Optional<Grade> deletedGrade = gradeRepository.findById(savedGrade.getId());
        assertFalse(deletedGrade.isPresent());
    }
}

