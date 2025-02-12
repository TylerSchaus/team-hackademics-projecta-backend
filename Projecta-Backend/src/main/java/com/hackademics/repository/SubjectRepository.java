package com.hackademics.repository;

import com.hackademics.Model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Optional<Subject> findBySubjectId(int subjectId);
    Optional<Subject> findBySubjectName(String subjectName);
}