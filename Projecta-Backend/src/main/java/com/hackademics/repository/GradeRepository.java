package com.hackademics.repository;

import com.hackademics.Model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, String> {
    List<Grade> findByCourseId(String courseId);
    List<Grade> findByStudentId(String studentId);
}
