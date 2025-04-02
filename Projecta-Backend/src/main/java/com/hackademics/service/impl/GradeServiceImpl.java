package com.hackademics.service.impl;

import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.RequestDto.GradeDto;
import com.hackademics.dto.ResponseDto.GradeResponseDto;
import com.hackademics.dto.UpdateDto.GradeUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.Grade;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.GradeService;
import com.hackademics.util.ConvertToResponseDto;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;


    @Override
    public GradeResponseDto saveGrade(GradeDto gradeDto, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only admins can create grades.");
        }

        if (gradeDto.getGrade() < 0 || gradeDto.getGrade() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid grade. Grade must be between 0 and 100.");
        }

        User student = userRepository.findByStudentId(gradeDto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + gradeDto.getStudentId()));
        
        Course course = courseRepository.findById(gradeDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + gradeDto.getCourseId()));
        
        Grade grade = new Grade(student, course, gradeDto.getGrade());
        return ConvertToResponseDto.convertToGradeResponseDto(gradeRepository.save(grade));
    }

    @Override
    public List<GradeResponseDto> getAllGrades(UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all grades.");
        }
        
        return gradeRepository.findAll().stream()
                .map(ConvertToResponseDto::convertToGradeResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public GradeResponseDto getGradeById(Long id, UserDetails currentUser) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found with ID: " + id));
        
        // Force loading of associations
        grade.getStudent().getId();
        grade.getCourse().getId();
        
        // Allow access if user is admin or if the grade belongs to the user
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, grade.getStudent().getStudentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. You can only view your own grades.");
        }
        
        return ConvertToResponseDto.convertToGradeResponseDto(grade);
    }

    @Override
    public GradeResponseDto updateGrade(Long id, GradeUpdateDto gradeUpdateDto, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only admins can update grades.");
        }
        
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found with ID: " + id));
        
        // Force loading of associations
        grade.getStudent().getId();
        grade.getCourse().getId();
        
        grade.setGrade(gradeUpdateDto.getGrade());
        return ConvertToResponseDto.convertToGradeResponseDto(gradeRepository.save(grade));
    }

    @Override
    public void deleteGrade(Long id, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only admins can delete grades.");
        }
        
        gradeRepository.deleteById(id);
    }

    @Override
    public List<GradeResponseDto> getGradesByStudentId(Long studentId, UserDetails currentUser) {
        // Allow access if user is admin or if the grades belong to the user
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. You can only view your own grades.");
        }
        
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return grades.stream()
                .map(grade -> {
                    // Force loading of associations
                    grade.getStudent().getId();
                    grade.getCourse().getId();
                    return ConvertToResponseDto.convertToGradeResponseDto(grade);
                })
                .collect(Collectors.toList());
    }
}
