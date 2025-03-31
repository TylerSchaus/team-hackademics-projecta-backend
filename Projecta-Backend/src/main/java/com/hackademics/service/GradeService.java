package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.GradeDto;
import com.hackademics.dto.GradeUpdateDto;
import com.hackademics.dto.GradeResponseDto;

public interface GradeService {

    GradeResponseDto saveGrade(GradeDto gradeDto, UserDetails currentUser); 
    List<GradeResponseDto> getAllGrades(UserDetails currentUser);
    GradeResponseDto getGradeById(Long id, UserDetails currentUser); 
    GradeResponseDto updateGrade(Long id, GradeUpdateDto gradeUpdateDto, UserDetails currentUser); 
    void deleteGrade(Long id, UserDetails currentUser); 
    List<GradeResponseDto> getGradesByStudentId(Long studentId, UserDetails currentUser);
    
}
