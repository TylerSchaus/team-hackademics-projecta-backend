package com.hackademics.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hackademics.dto.RequestDto.WaitlistEnrollmentDto;
import com.hackademics.dto.ResponseDto.WaitlistEnrollmentResponseDto;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistEnrollmentRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.WaitlistEnrollmentService;
import com.hackademics.util.ConvertToResponseDto;
import com.hackademics.util.EmailSender;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class WaitlistEnrollmentServiceImpl implements WaitlistEnrollmentService {

    @Autowired
    private WaitlistEnrollmentRepository waitlistEnrollmentRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    @Autowired
    private EmailSender emailSender;

    @Value("${email.sending.enabled:true}")
    private boolean emailSendingEnabled;


    @Override
    public WaitlistEnrollmentResponseDto saveWaitlistEnrollment(WaitlistEnrollmentDto waitlistEnrollmentDto, UserDetails currentUser) {
        
        // Get the waitlist
        Waitlist waitlist = waitlistRepository.findById(waitlistEnrollmentDto.getWaitlistId())
                .orElseThrow(() -> new RuntimeException("Waitlist not found with ID: " + waitlistEnrollmentDto.getWaitlistId()));
        
        // Get the student
        User student = userRepository.findByStudentId(waitlistEnrollmentDto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + waitlistEnrollmentDto.getStudentId()));
        
        // Check if user is admin or the student themselves
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, student.getStudentId())) {
            throw new RuntimeException("Access denied. Only admins or the student themselves can create waitlist enrollments.");
        }
        
        // Count current enrollments
        List<WaitlistEnrollment> currentEnrollments = waitlistEnrollmentRepository.findByWaitlistId(waitlist.getId());
        
        // Check capacity
        if (currentEnrollments.size() >= waitlist.getWaitlistLimit()) {
            throw new RuntimeException("Waitlist is at capacity. Cannot add more enrollments.");
        }
        
        // Create and save the enrollment
        WaitlistEnrollment enrollment = new WaitlistEnrollment(currentEnrollments.size() + 1, waitlist, student);
        if (emailSendingEnabled) {
            emailSender.sendWaitlistEmail(enrollment);
        }
        return ConvertToResponseDto.convertToWaitlistEnrollmentResponseDto(waitlistEnrollmentRepository.save(enrollment));
    }

    @Override
    public void deleteWaitlistEnrollment(Long id, UserDetails currentUser) {
        
        WaitlistEnrollment enrollment = waitlistEnrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WaitlistEnrollment not found with ID: " + id));
        
        // Allow deletion if user is admin or if the enrollment belongs to the student
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, enrollment.getStudent().getStudentId())) {
            throw new RuntimeException("Access denied. You can only delete your own waitlist enrollments.");
        }

        // Delete the enrollment and update the waitlist positions
        waitlistEnrollmentRepository.deleteById(id);
        updateWaitlistPositions(enrollment.getWaitlist().getId());
        if (emailSendingEnabled) {
            emailSender.sendWaitlistRemovalEmail(enrollment);
        }
    }

    @Override
    public List<WaitlistEnrollmentResponseDto> getWaitlistEnrollmentsByStudentId(Long studentId, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, studentId)) {
            throw new RuntimeException("Access denied. Only admins or the student themselves can view waitlist enrollments.");
        }
        
        return waitlistEnrollmentRepository.findByStudentId(studentId).stream()
                .map(ConvertToResponseDto::convertToWaitlistEnrollmentResponseDto)
                .collect(Collectors.toList());
    }

    private void updateWaitlistPositions(Long waitlistId) {
        List<WaitlistEnrollment> enrollments = waitlistEnrollmentRepository.findByWaitlistId(waitlistId).stream()
                .sorted(Comparator.comparingInt(WaitlistEnrollment::getWaitlistPosition))
                .collect(Collectors.toList());
        
        for (int i = 0; i < enrollments.size(); i++) {
            WaitlistEnrollment enrollment = enrollments.get(i);
            enrollment.setWaitlistPosition(i + 1);
            waitlistEnrollmentRepository.save(enrollment);
        }
    }

    
}
