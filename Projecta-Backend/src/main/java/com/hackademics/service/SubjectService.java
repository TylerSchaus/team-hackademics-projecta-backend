package com.hackademics.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.SubjectDto;
import com.hackademics.dto.SubjectResponseDto;
import com.hackademics.dto.SubjectUpdateDto;

public interface SubjectService {
    List<SubjectResponseDto> getAllSubjects();
    Optional<SubjectResponseDto> getSubjectById(Long id);
    Optional<SubjectResponseDto> updateSubject(Long id, SubjectUpdateDto updatedSubjectDto, UserDetails currentUser);
    SubjectResponseDto createSubject(SubjectDto subjectDto, UserDetails currentUser);
    boolean deleteSubject(Long id, UserDetails currentUser);
}