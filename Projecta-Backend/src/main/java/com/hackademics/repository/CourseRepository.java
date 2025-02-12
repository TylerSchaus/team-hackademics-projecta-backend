package com.hackademics.repository;

import com.hackademics.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByCourseId(String courseId);
    List<Course> findByProfessorId(String professorId);
    List<Course> findBySubjectId(int subjectId);
}
