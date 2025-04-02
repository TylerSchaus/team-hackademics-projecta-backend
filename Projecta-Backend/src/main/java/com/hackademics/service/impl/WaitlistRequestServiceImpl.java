package com.hackademics.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.RequestDto.WaitlistRequestDto;
import com.hackademics.dto.ResponseDto.WaitlistRequestResponseDto;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistRequest;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.repository.WaitlistRequestRepository;
import com.hackademics.service.WaitlistRequestService;
import com.hackademics.util.ConvertToResponseDto;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class WaitlistRequestServiceImpl implements WaitlistRequestService {

    @Autowired
    private WaitlistRequestRepository waitlistRequestRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    @Override
    public WaitlistRequestResponseDto saveWaitlistRequest(WaitlistRequestDto waitlistRequestDto, UserDetails currentUser) {
        // Get the waitlist
        Waitlist waitlist = waitlistRepository.findById(waitlistRequestDto.getWaitlistId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Waitlist not found"));

        // Get the student
        User student = userRepository.findByStudentId(waitlistRequestDto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // Check if user is admin or the student themselves
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, student.getStudentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only admins or the student themselves can create waitlist requests.");
        }

        // Create and save the request
        WaitlistRequest request = new WaitlistRequest(waitlist, student);
        return ConvertToResponseDto.convertToWaitlistRequestResponseDto(waitlistRequestRepository.save(request));
    }

    @Override
    public List<WaitlistRequestResponseDto> getAllWaitlistRequests(UserDetails currentUser) {
        // Only admins can view all waitlist requests
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all waitlist requests.");
        }

        return waitlistRequestRepository.findAll().stream()
                .map(ConvertToResponseDto::convertToWaitlistRequestResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteWaitlistRequest(Long id, UserDetails currentUser) {
        WaitlistRequest request = waitlistRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Waitlist request not found"));

        // Allow deletion if user is admin or if the request belongs to the student
        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, request.getStudent().getStudentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. You can only delete your own waitlist requests.");
        }

        waitlistRequestRepository.deleteById(id);
    }
}
