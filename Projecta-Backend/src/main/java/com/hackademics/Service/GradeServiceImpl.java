package com.hackademics.Service;

import com.hackademics.model.Grade;
import com.hackademics.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Override
    @Secured("ROLE_ADMIN") // Ensure only admins can create grades
    public Grade saveGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    @Override
    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + id));
    }

    @Override
    public Grade updateGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    @Secured("ROLE_ADMIN") // Ensure only admins can delete grades
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }
}