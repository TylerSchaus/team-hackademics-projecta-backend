package com.hackademics.repository;

import com.hackademics.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectId(Long subjectId);
    Optional<Subject> findBySubjectName(String subjectName);
}