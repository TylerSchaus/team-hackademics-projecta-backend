package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.RequestDto.EnrollmentDto;
import com.hackademics.dto.ResponseDto.EnrollmentResponseDto;

public interface EnrollmentService {
    EnrollmentResponseDto saveEnrollment(EnrollmentDto enrollmentDto, UserDetails currentUser);
    List<EnrollmentResponseDto> getAllEnrollments(UserDetails currentUser);
    EnrollmentResponseDto getEnrollmentById(Long id, UserDetails currentUser);
    List<EnrollmentResponseDto> getEnrollmentsByCourseId(Long id, UserDetails currentUser);
    List<EnrollmentResponseDto> getEnrollmentsByStudentId(Long id, UserDetails currentUser);
    void deleteEnrollment(Long id, UserDetails currentUser);
    List<EnrollmentResponseDto> getCurrentEnrollmentByStudentId(UserDetails currentUser, Long studentId, String term);
}