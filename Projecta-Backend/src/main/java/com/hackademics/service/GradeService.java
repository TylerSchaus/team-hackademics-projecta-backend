package com.hackademics.service;

import java.util.List;

import com.hackademics.model.Grade;

public interface GradeService {

    Grade saveGrade(Grade grade); 
    List<Grade> getAllGrades();
    Grade getGradeById(Long id); 
    Grade updateGrade(Grade grade); 
    void deleteGrade(Long id); 
    
}
