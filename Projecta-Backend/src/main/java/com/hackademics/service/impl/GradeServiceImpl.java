package com.hackademics.service.impl;

import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hackademics.dto.RequestDto.GradeDto;
import com.hackademics.dto.ResponseDto.GradeResponseDto;
import com.hackademics.dto.ResponseDto.StudentSummaryDto;
import com.hackademics.dto.UpdateDto.GradeUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.Grade;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.CourseService;
import com.hackademics.service.GradeService;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private CourseService courseService;
    
    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    private GradeResponseDto convertToGradeResponseDto(Grade grade) {
        return new GradeResponseDto(
            grade.getId(),
            grade.getGrade(),
            new StudentSummaryDto(grade.getStudent().getId(), grade.getStudent().getFirstName(), grade.getStudent().getLastName(), grade.getStudentId()),
            courseService.convertToResponseDto(grade.getCourse())
        );
    }

    @Override
    public GradeResponseDto saveGrade(GradeDto gradeDto, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can create grades.");
        }

        User student = userRepository.findByStudentId(gradeDto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + gradeDto.getStudentId()));
        
        Course course = courseRepository.findById(gradeDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + gradeDto.getCourseId()));
        
        Grade grade = new Grade(student, course, gradeDto.getGrade());
        return convertToGradeResponseDto(gradeRepository.save(grade));
    }

    @Override
    public List<GradeResponseDto> getAllGrades(UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can view all grades.");
        }
        
        return gradeRepository.findAll().stream()
                .map(this::convertToGradeResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public GradeResponseDto getGradeById(Long id, UserDetails currentUser) {
        
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + id));
        
        // Allow access if user is admin or if the grade belongs to the user
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, grade.getStudent().getStudentId())) {
            throw new RuntimeException("Access denied. You can only view your own grades.");
        }
        
        return convertToGradeResponseDto(grade);
    }

    @Override
    public GradeResponseDto updateGrade(Long id, GradeUpdateDto gradeUpdateDto, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can update grades.");
        }
        
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + id));
        
        grade.setGrade(gradeUpdateDto.getGrade());
        return convertToGradeResponseDto(gradeRepository.save(grade));
    }

    @Override
    public void deleteGrade(Long id, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can delete grades.");
        }
        
        gradeRepository.deleteById(id);
    }

    @Override
    public List<GradeResponseDto> getGradesByStudentId(Long studentId, UserDetails currentUser) {
        
        // Allow access if user is admin or if the grades belong to the user
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, studentId)) {
            throw new RuntimeException("Access denied. You can only view your own grades.");
        }
        
        return gradeRepository.findAll().stream()
                .filter(grade -> grade.getStudent().getStudentId().equals(studentId))
                .map(this::convertToGradeResponseDto)
                .collect(Collectors.toList());
    }
}
