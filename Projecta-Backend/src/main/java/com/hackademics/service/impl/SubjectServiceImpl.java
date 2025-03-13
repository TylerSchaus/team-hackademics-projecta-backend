package com.hackademics.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackademics.dto.SubjectDto;
import com.hackademics.model.Subject;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.service.SubjectService;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject createSubject(SubjectDto subjectDto) {
        Subject newSubject = new Subject(subjectDto.getSubjectName(), subjectDto.getSubjectTag());
        return subjectRepository.save(newSubject);
    }

    @Override
    public Optional<Subject> updateSubject(Long id, SubjectDto updatedSubjectDto) {
        return subjectRepository.findById(id).map(subject -> {
            subject.setSubjectName(updatedSubjectDto.getSubjectName());
            subject.setSubjectTag(updatedSubjectDto.getSubjectTag());
            return subjectRepository.save(subject);
        });
    }

    @Override
    public boolean deleteSubject(Long id) {
        if (subjectRepository.existsById(id)) {
            subjectRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
