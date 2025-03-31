package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.WaitlistEnrollmentDto;
import com.hackademics.dto.WaitlistEnrollmentResponseDto;

public interface WaitlistEnrollmentService {
    WaitlistEnrollmentResponseDto saveWaitlistEnrollment(WaitlistEnrollmentDto waitlistEnrollmentDto, UserDetails currentUser);
    void deleteWaitlistEnrollment(Long id, UserDetails currentUser);
    List<WaitlistEnrollmentResponseDto> getWaitlistEnrollmentsByStudentId(Long studentId, UserDetails currentUser);
}

