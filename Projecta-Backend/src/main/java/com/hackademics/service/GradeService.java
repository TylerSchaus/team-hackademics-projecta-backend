package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.GradeDto;
import com.hackademics.dto.GradeUpdateDto;
import com.hackademics.model.Grade;

public interface GradeService {

    Grade saveGrade(GradeDto gradeDto, UserDetails currentUser); 
    List<Grade> getAllGrades(UserDetails currentUser);
    Grade getGradeById(Long id, UserDetails currentUser); 
    Grade updateGrade(Long id, GradeUpdateDto gradeUpdateDto, UserDetails currentUser); 
    void deleteGrade(Long id, UserDetails currentUser); 
    List<Grade> getGradesByStudentId(Long studentId, UserDetails currentUser);
    
}
