package com.hackademics.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.RequestDto.SubjectDto;
import com.hackademics.dto.ResponseDto.SubjectResponseDto;
import com.hackademics.dto.UpdateDto.SubjectUpdateDto;

public interface SubjectService {
    List<SubjectResponseDto> getAllSubjects();
    Optional<SubjectResponseDto> getSubjectById(Long id);
    Optional<SubjectResponseDto> updateSubject(Long id, SubjectUpdateDto updatedSubjectDto, UserDetails currentUser);
    SubjectResponseDto createSubject(SubjectDto subjectDto, UserDetails currentUser);
    boolean deleteSubject(Long id, UserDetails currentUser);
}