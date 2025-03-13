package com.hackademics.service;

import java.util.List;
import java.util.Optional;

import com.hackademics.dto.SubjectDto;
import com.hackademics.model.Subject;

public interface SubjectService {

    List<Subject> getAllSubjects();
    Optional<Subject> getSubjectById(Long id);
    Optional<Subject> updateSubject(Long id, SubjectDto updatedSubjectDto);
    Subject createSubject(SubjectDto subjectDto);
    boolean deleteSubject(Long id);
}