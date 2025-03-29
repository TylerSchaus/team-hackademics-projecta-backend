package com.hackademics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByCourseId(Long id);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByTermAndStudentId(String term, Long studentId);
}
