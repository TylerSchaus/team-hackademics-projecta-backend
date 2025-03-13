package com.hackademics.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.SubjectDto;
import com.hackademics.model.Subject;

public interface SubjectService {

    List<Subject> getAllSubjects();
    Optional<Subject> getSubjectById(Long id);
    Optional<Subject> updateSubject(Long id, SubjectDto updatedSubjectDto, UserDetails currentUser);
    Subject createSubject(SubjectDto subjectDto, UserDetails currentUser);
    boolean deleteSubject(Long id, UserDetails currentUser);
}