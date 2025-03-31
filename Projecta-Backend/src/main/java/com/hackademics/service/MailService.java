package com.hackademics.service;

import com.hackademics.dto.ResponseDto.EnrollmentResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistEnrollmentResponseDto;
import com.hackademics.model.User;


public interface MailService {
    
    void sendEnrollmentEmail(EnrollmentResponseDto enrollmentResponseDto, User student); 
    void sendWaitlistEmail(WaitlistEnrollmentResponseDto waitlistEnrollmentResponseDto, User student);
    void sendEnrollmentRemovalEmail(EnrollmentResponseDto enrollmentResponseDto, User student);
    void sendWaitlistRemovalEmail(WaitlistEnrollmentResponseDto waitlistEnrollmentResponseDto, User student);
}
