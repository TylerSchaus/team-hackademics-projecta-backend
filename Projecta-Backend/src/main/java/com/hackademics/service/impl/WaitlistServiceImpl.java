package com.hackademics.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hackademics.dto.WaitlistDto;
import com.hackademics.dto.WaitlistUpdateDto;
import com.hackademics.dto.WaitlistResponseDto;
import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.WaitlistService;
import com.hackademics.service.CourseService;

@Service
public class WaitlistServiceImpl implements WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    private WaitlistResponseDto convertToResponseDto(Waitlist waitlist) {
        return new WaitlistResponseDto(
            waitlist.getId(),
            waitlist.getWaitlistLimit(),
            courseService.convertToResponseDto(waitlist.getCourse())
        );
    }

    @Override
    public WaitlistResponseDto saveWaitlist(WaitlistDto waitlistDto, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can create waitlists.");
        }

        Course course = courseRepository.findById(waitlistDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + waitlistDto.getCourseId()));


        Waitlist waitlist = new Waitlist(course, waitlistDto.getCapacity());

        return convertToResponseDto(waitlistRepository.save(waitlist));
    }

    @Override
    public List<WaitlistResponseDto> getAllWaitlists(UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can view all waitlists.");
        }
        
        return waitlistRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public WaitlistResponseDto getWaitlistById(Long id, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Waitlist waitlist = waitlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waitlist not found with ID: " + id));
        
        // Allow access if user is admin or if the waitlist belongs to the user's course
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can view waitlists.");
        }
        
        return convertToResponseDto(waitlist);
    }

    @Override
    public WaitlistResponseDto updateWaitlist(Long id, WaitlistUpdateDto waitlistUpdateDto, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can update waitlists.");
        }
        
        Waitlist waitlist = waitlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waitlist not found with ID: " + id));
        
        waitlist.setWaitlistLimit(waitlistUpdateDto.getCapacity());
        return convertToResponseDto(waitlistRepository.save(waitlist));
    }

    @Override
    public void deleteWaitlist(Long id, UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can delete waitlists.");
        }
        
        waitlistRepository.deleteById(id);
    }
}
