package com.hackademics.Service;

import com.hackademics.model.Subject;
import java.util.List;

public interface SubjectService {
    Subject saveSubject(Subject subject);
    List<Subject> getAllSubjects();
    Subject getSubjectById(Long id);
    Subject updateSubject(Subject subject);
    void deleteSubject(Long id);
}
