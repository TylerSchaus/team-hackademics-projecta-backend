package com.hackademics.service.impl;

import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hackademics.dto.GradeDto;
import com.hackademics.dto.GradeUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.Grade;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.GradeService;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Grade saveGrade(GradeDto gradeDto, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can create grades.");
        }

        User student = userRepository.findById(gradeDto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + gradeDto.getStudentId()));
        
        Course course = courseRepository.findById(gradeDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + gradeDto.getCourseId()));
        
        Grade grade = new Grade(student, course, gradeDto.getGrade());
        return gradeRepository.save(grade);
    }

    @Override
    public List<Grade> getAllGrades(UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can view all grades.");
        }
        
        return gradeRepository.findAll();
    }

    @Override
    public Grade getGradeById(Long id, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can view grade details.");
        }
        
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + id));
    }

    @Override
    public Grade updateGrade(Long id, GradeUpdateDto gradeUpdateDto, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can update grades.");
        }
        
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + id));
        
        grade.setGrade(gradeUpdateDto.getGrade());
        return gradeRepository.save(grade);
    }

    @Override
    public void deleteGrade(Long id, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can delete grades.");
        }
        
        gradeRepository.deleteById(id);
    }

    @Override
    public List<Grade> getGradesByStudentId(Long studentId, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN && !user.getId().equals(studentId)) {
            throw new RuntimeException("Access denied. You can only view your own grades.");
        }
        
        return gradeRepository.findAll().stream()
                .filter(grade -> grade.getStudent().getId().equals(studentId))
                .collect(Collectors.toList());
    }
}
