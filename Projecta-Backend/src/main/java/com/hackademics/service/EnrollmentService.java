package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.EnrollmentDto;
import com.hackademics.model.Enrollment;

public interface EnrollmentService {
    Enrollment saveEnrollment(EnrollmentDto enrollmentDto, UserDetails currentUser);
    List<Enrollment> getAllEnrollments(UserDetails currentUser);
    Enrollment getEnrollmentById(Long id, UserDetails currentUser);
    List<Enrollment> getEnrollmentsByCourseId(Long id, UserDetails currentUser); 
    void deleteEnrollment(Long id, UserDetails currentUser);
}