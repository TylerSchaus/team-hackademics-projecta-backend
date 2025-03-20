package com.hackademics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hackademics.model.LabSection;

@Repository
public interface LabSectionRepository extends JpaRepository<LabSection, Long> {
    List<LabSection> findByCourseId(Long id);

    @Query("SELECT MAX(l.sectionId) FROM LabSection l WHERE l.course.id = :courseId")
    Long findMaxLabSectionIdForCourse(@Param("courseId") Long courseId);
}
