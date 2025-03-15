package com.hackademics.service;

import java.util.List;

import com.hackademics.model.Subject;

public interface SubjectService {
    Subject saveSubject(Subject subject);
    List<Subject> getAllSubjects();
    Subject getSubjectById(Long id);
    Subject updateSubject(Subject subject);
    void deleteSubject(Long id);
}