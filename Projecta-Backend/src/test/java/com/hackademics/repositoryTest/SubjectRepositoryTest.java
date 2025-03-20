package com.hackademics.repositoryTest;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.hackademics.model.Subject;
import com.hackademics.repository.SubjectRepository;

@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based test profile
class SubjectRepositoryTest {

    @Autowired
    private SubjectRepository subjectRepository;

    private Subject subject1;
    private Subject subject2;

    @BeforeEach
    void setUp() {
        // Create subjects using constructors
        subject1 = new Subject("Mathematics", "MATH");
        subject2 = new Subject("Physics", "PHYS");
    }

    @Test
    void testSaveSubject() {
        // Save the subject
        Subject savedSubject = subjectRepository.save(subject1);

        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedSubject.getId(), "Saved subject should have an ID");

        // Check that fields match
        Assertions.assertEquals("Mathematics", savedSubject.getSubjectName(), "Subject name should match");
    }

    @Test
    void testGetSubjectById() {
        // Save a subject
        Subject saved = subjectRepository.save(subject1);
        Long id = saved.getId();

        // Retrieve by that ID
        Optional<Subject> found = subjectRepository.findById(id);

        Assertions.assertTrue(found.isPresent(), "Subject should exist by that primary ID");
        Assertions.assertEquals("Mathematics", found.get().getSubjectName(), "Subject name should match");
    }

    @Test
    void testUpdateSubject() {
        // Save the subject initially
        Subject saved = subjectRepository.save(subject1);
        Long id = saved.getId();

        // Modify some fields
        saved.setSubjectName("Advanced Mathematics");
        subjectRepository.save(saved);

        // Fetch again
        Subject updated = subjectRepository.findById(id).orElseThrow();
        Assertions.assertEquals("Advanced Mathematics", updated.getSubjectName(), "Updated subject name should match");
    }

    @Test
    void testDeleteSubject() {
        // Save the subject
        Subject saved = subjectRepository.save(subject1);
        Long id = saved.getId();

        // Delete the subject
        subjectRepository.deleteById(id);

        // Verify the subject is deleted
        Optional<Subject> afterDelete = subjectRepository.findById(id);
        Assertions.assertTrue(afterDelete.isEmpty(), "Subject should be deleted from the database");
    }

    @Test
    void testFindSubjectByName(){
        Subject saved = subjectRepository.save(subject1); 
        Optional<Subject> found = subjectRepository.findBySubjectName("Mathematics");
        Assertions.assertTrue(found.isPresent(), "Subject should exist by that name");
        Assertions.assertEquals("Mathematics", found.get().getSubjectName(), "Subject name should match");
    }

    @Test 
    void testFindSubjectByTag(){
        Subject saved = subjectRepository.save(subject1); 
        Optional<Subject> found = subjectRepository.findBySubjectTag("MATH");
        Assertions.assertTrue(found.isPresent(), "Subject should exist by that tag");
        Assertions.assertEquals("MATH", found.get().getSubjectTag(), "Subject tag should match");
    }
}

