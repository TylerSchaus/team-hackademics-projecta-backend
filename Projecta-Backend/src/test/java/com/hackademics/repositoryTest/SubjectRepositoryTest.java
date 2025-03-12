package com.hackademics.repositoryTest;


import com.hackademics.model.Subject;
import com.hackademics.repository.SubjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based application-test.properties
class SubjectRepositoryTest {


    @Autowired
    private SubjectRepository subjectRepository;


    private Subject subject1;
    private Subject subject2;


    @BeforeEach
    void setUp() {
        // Create a subject
        subject1 = new Subject();
        subject1.setSubjectName("Mathematics");


        // Create another subject
        subject2 = new Subject();
        subject2.setSubjectName("Physics");
    }


    @Test
    void testSaveSubject() {
        // Save the subject
        Subject savedSubject = subjectRepository.save(subject1);


        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedSubject.getId(), "Saved subject should have an ID");
        // Check that fields match
        Assertions.assertEquals("Mathematics", savedSubject.getSubjectName());
    }


    @Test
    void testGetSubjectById() {
        // Save a subject
        Subject saved = subjectRepository.save(subject1);
        Long id = saved.getId();


        // Retrieve by that ID
        Optional<Subject> found = subjectRepository.findById(id);
        Assertions.assertTrue(found.isPresent(), "Subject should exist by that primary ID");
        Assertions.assertEquals("Mathematics", found.get().getSubjectName());
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
        Assertions.assertEquals("Advanced Mathematics", updated.getSubjectName());
    }


    @Test
    void testDeleteSubject() {
        Subject saved = subjectRepository.save(subject1);
        Long id = saved.getId();


        subjectRepository.deleteById(id);


        Optional<Subject> afterDelete = subjectRepository.findById(id);
        Assertions.assertTrue(afterDelete.isEmpty(), "Subject should be deleted from the database");
    }
}



