package com.hackademics.Service;

import com.hackademics.model.Grade;
import java.util.List;

public interface GradeService {
    Grade saveGrade(Grade grade);
    List<Grade> getAllGrades();
    Grade getGradeById(Long id);
    Grade updateGrade(Grade grade);
    void deleteGrade(Long id);
}