package com.hackademics.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByAdminId(Long id);
    List<Course> findBySubjectId(Long id);
    List<Course> findByStartDateAfter(LocalDateTime now);
    List<Course> findByStartDateBeforeAndEndDateAfter(LocalDateTime now1, LocalDateTime now2);
}
