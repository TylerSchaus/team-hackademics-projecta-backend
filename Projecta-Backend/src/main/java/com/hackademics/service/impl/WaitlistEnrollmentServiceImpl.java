package com.hackademics.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hackademics.dto.RequestDto.WaitlistEnrollmentDto;
import com.hackademics.dto.ResponseDto.AdminSummaryDto;
import com.hackademics.dto.ResponseDto.CourseResponseDto;
import com.hackademics.dto.ResponseDto.StudentSummaryDto;
import com.hackademics.dto.ResponseDto.SubjectResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistEnrollmentResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistResponseDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistEnrollmentRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.WaitlistEnrollmentService;

@Service
public class WaitlistEnrollmentServiceImpl implements WaitlistEnrollmentService {

    @Autowired
    private WaitlistEnrollmentRepository waitlistEnrollmentRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    private WaitlistEnrollmentResponseDto convertToResponseDto(WaitlistEnrollment enrollment) {
        return new WaitlistEnrollmentResponseDto(
            enrollment.getId(),
            enrollment.getWaitlistPosition(),
            new WaitlistResponseDto(
                enrollment.getWaitlist().getId(),
                enrollment.getWaitlist().getWaitlistLimit(),
                new CourseResponseDto(
                    enrollment.getWaitlist().getCourse().getId(),
                    new AdminSummaryDto(
                        enrollment.getWaitlist().getCourse().getAdmin().getId(),
                        enrollment.getWaitlist().getCourse().getAdmin().getFirstName(),
                        enrollment.getWaitlist().getCourse().getAdmin().getLastName(),
                        enrollment.getWaitlist().getCourse().getAdmin().getAdminId()
                    ),
                    new SubjectResponseDto(
                        enrollment.getWaitlist().getCourse().getSubject().getId(),
                        enrollment.getWaitlist().getCourse().getSubject().getSubjectName(),
                        enrollment.getWaitlist().getCourse().getSubject().getSubjectTag()
                    ),
                    enrollment.getWaitlist().getCourse().getCourseName(),
                    enrollment.getWaitlist().getCourse().getStartDate().toLocalDate(),
                    enrollment.getWaitlist().getCourse().getEndDate().toLocalDate(),
                    enrollment.getWaitlist().getCourse().getEnrollLimit(),
                    enrollment.getWaitlist().getCourse().getCurrentEnroll(),
                    enrollment.getWaitlist().getCourse().getCourseNumber(),
                    enrollment.getWaitlist().getCourse().getCourseTag(),
                    enrollment.getWaitlist().getCourse().getTerm(),
                    enrollment.getWaitlist().getCourse().getDays(),
                    enrollment.getWaitlist().getCourse().getStartTime(),
                    enrollment.getWaitlist().getCourse().getEndTime(),
                    enrollment.getWaitlist().getCourse().getNumLabSections()
                )
            ),
            new StudentSummaryDto(
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFirstName(),
                enrollment.getStudent().getLastName(),
                enrollment.getStudent().getStudentId()
            ),
            enrollment.getTerm()
        );
    }

    @Override
    public WaitlistEnrollmentResponseDto saveWaitlistEnrollment(WaitlistEnrollmentDto waitlistEnrollmentDto, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get the waitlist
        Waitlist waitlist = waitlistRepository.findById(waitlistEnrollmentDto.getWaitlistId())
                .orElseThrow(() -> new RuntimeException("Waitlist not found with ID: " + waitlistEnrollmentDto.getWaitlistId()));
        
        // Get the student
        User student = userRepository.findByStudentId(waitlistEnrollmentDto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + waitlistEnrollmentDto.getStudentId()));
        
        // Check if user is admin or the student themselves
        if (user.getRole() != Role.ADMIN && !user.getStudentId().equals(student.getStudentId())) {
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
        return convertToResponseDto(waitlistEnrollmentRepository.save(enrollment));
    }

    @Override
    public void deleteWaitlistEnrollment(Long id, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        WaitlistEnrollment enrollment = waitlistEnrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WaitlistEnrollment not found with ID: " + id));
        
        // Allow deletion if user is admin or if the enrollment belongs to the student
        if (user.getRole() != Role.ADMIN && !user.getStudentId().equals(enrollment.getStudent().getStudentId())) {
            throw new RuntimeException("Access denied. You can only delete your own waitlist enrollments.");
        }

        // Delete the enrollment and update the waitlist positions
        waitlistEnrollmentRepository.deleteById(id);
        updateWaitlistPositions(enrollment.getWaitlist().getId());
    }

    @Override
    public List<WaitlistEnrollmentResponseDto> getWaitlistEnrollmentsByStudentId(Long studentId, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN && !user.getStudentId().equals(studentId)) {
            throw new RuntimeException("Access denied. Only admins or the student themselves can view waitlist enrollments.");
        }
        
        return waitlistEnrollmentRepository.findByStudentId(studentId).stream()
                .map(this::convertToResponseDto)
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
