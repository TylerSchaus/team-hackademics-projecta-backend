package com.hackademics.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hackademics.dto.RequestDto.WaitlistDto;
import com.hackademics.dto.ResponseDto.AdminSummaryDto;
import com.hackademics.dto.ResponseDto.CourseResponseDto;
import com.hackademics.dto.ResponseDto.SubjectResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistResponseDto;
import com.hackademics.dto.UpdateDto.WaitlistUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.Waitlist;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.WaitlistService;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class WaitlistServiceImpl implements WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    private CourseResponseDto convertCourseToResponseDto(Course course) {
        AdminSummaryDto adminDto = new AdminSummaryDto(
            course.getAdmin().getId(),
            course.getAdmin().getFirstName(),
            course.getAdmin().getLastName(),
            course.getAdmin().getAdminId()
        );

        SubjectResponseDto subjectDto = new SubjectResponseDto(
            course.getSubject().getId(),
            course.getSubject().getSubjectName(),
            course.getSubject().getSubjectTag()
        );

        return new CourseResponseDto(
            course.getId(),
            adminDto,
            subjectDto,
            course.getCourseName(),
            course.getStartDate().toLocalDate(),
            course.getEndDate().toLocalDate(),
            course.getEnrollLimit(),
            course.getCurrentEnroll(),
            course.getCourseNumber(),
            course.getCourseTag(),
            course.getTerm(),
            course.getDays(),
            course.getStartTime(),
            course.getEndTime(),
            course.getNumLabSections()
        );
    }

    @Override
    public WaitlistResponseDto convertToResponseDto(Waitlist waitlist) {
        return new WaitlistResponseDto(
            waitlist.getId(),
            waitlist.getWaitlistLimit(),
            convertCourseToResponseDto(waitlist.getCourse())
        );
    }

    @Override
    public WaitlistResponseDto saveWaitlist(WaitlistDto waitlistDto, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can create waitlists.");
        }

        Course course = courseRepository.findById(waitlistDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + waitlistDto.getCourseId()));

        Waitlist waitlist = new Waitlist(course, waitlistDto.getCapacity());

        return convertToResponseDto(waitlistRepository.save(waitlist));
    }

    @Override
    public List<WaitlistResponseDto> getAllWaitlists(UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can view all waitlists.");
        }
        
        return waitlistRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public WaitlistResponseDto getWaitlistById(Long id, UserDetails currentUser) {
        
        Waitlist waitlist = waitlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waitlist not found with ID: " + id));
        
        // Allow access if user is admin or if the waitlist belongs to the user's course
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can view waitlists.");
        }
        
        return convertToResponseDto(waitlist);
    }

    @Override
    public WaitlistResponseDto updateWaitlist(Long id, WaitlistUpdateDto waitlistUpdateDto, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can update waitlists.");
        }
        
        Waitlist waitlist = waitlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waitlist not found with ID: " + id));
        
        waitlist.setWaitlistLimit(waitlistUpdateDto.getCapacity());
        return convertToResponseDto(waitlistRepository.save(waitlist));
    }

    @Override
    public void deleteWaitlist(Long id, UserDetails currentUser) {
        
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new RuntimeException("Access denied. Only admins can delete waitlists.");
        }
        
        Waitlist waitlist = waitlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waitlist not found with ID: " + id));
        
        waitlist.getCourse().setWaitlistAvailable(false);
        courseRepository.save(waitlist.getCourse());
        waitlistRepository.deleteById(id);
    }

  
}
