package com.hackademics.repositoryTest;

import com.hackademics.Model.Subject;
import com.hackademics.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Enables JPA testing with an in-memory database
public class SubjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Manages persistence context for tests

    @Autowired
    private SubjectRepository subjectRepository; // Repository to be tested

    @Test
    public void testFindById() {
        // Arrange
        Subject subject = new Subject();
        subject.setSubjectName("Mathematics");
        entityManager.persist(subject); // Persist the subject to the in-memory database

        // Act
        Optional<Subject> foundSubject = subjectRepository.findById(subject.getSubjectId());

        // Assert
        assertThat(foundSubject).isPresent(); // Verify the subject is found
        assertThat(foundSubject.get().getSubjectName()).isEqualTo("Mathematics"); // Verify the subject name matches
    }

    @Test
    public void testFindBySubjectName() {
        // Arrange
        Subject subject = new Subject();
        subject.setSubjectName("Physics");
        entityManager.persist(subject); // Persist the subject to the in-memory database

        // Act
        Optional<Subject> foundSubject = subjectRepository.findBySubjectName("Physics");

        // Assert
        assertThat(foundSubject).isPresent(); // Verify the subject is found
        assertThat(foundSubject.get().getSubjectName()).isEqualTo("Physics"); // Verify the subject name matches
    }

    @Test
    public void testSaveSubject() {
        // Arrange
        Subject subject = new Subject();
        subject.setSubjectName("Chemistry");

        // Act
        Subject savedSubject = subjectRepository.save(subject);

        // Assert
        assertThat(savedSubject.getSubjectId()).isNotNull(); // Verify the subject ID is generated
        assertThat(savedSubject.getSubjectName()).isEqualTo("Chemistry"); // Verify the subject name matches
    }

    @Test
    public void testDeleteSubject() {
        // Arrange
        Subject subject = new Subject();
        subject.setSubjectName("Biology");
        entityManager.persist(subject); // Persist the subject to the in-memory database

        // Act
        subjectRepository.delete(subject);

        // Assert
        Optional<Subject> deletedSubject = subjectRepository.findById(subject.getSubjectId());
        assertThat(deletedSubject).isEmpty(); // Verify the subject is deleted
    }
}