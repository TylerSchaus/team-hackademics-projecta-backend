package com.hackademics.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.RequestDto.WaitlistDto;
import com.hackademics.dto.ResponseDto.WaitlistResponseDto;
import com.hackademics.dto.UpdateDto.WaitlistUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.Waitlist;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.WaitlistService;
import com.hackademics.util.ConvertToResponseDto;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class WaitlistServiceImpl implements WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    @Override
    public WaitlistResponseDto saveWaitlist(WaitlistDto waitlistDto, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create waitlists.");
        }

        Course course = courseRepository.findById(waitlistDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        Waitlist waitlist = new Waitlist(course, waitlistDto.getCapacity());
        return ConvertToResponseDto.convertWaitlistToResponseDto(waitlistRepository.save(waitlist), course);
    }

    @Override
    public List<WaitlistResponseDto> getAllWaitlists(UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all waitlists.");
        }

        return waitlistRepository.findAll().stream()
                .map(waitlist -> ConvertToResponseDto.convertWaitlistToResponseDto(waitlist, waitlist.getCourse()))
                .collect(Collectors.toList());
    }

    @Override
    public WaitlistResponseDto getWaitlistById(Long id, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view waitlist details.");
        }

        Waitlist waitlist = waitlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Waitlist not found"));
        return ConvertToResponseDto.convertWaitlistToResponseDto(waitlist, waitlist.getCourse());
    }

    @Override
    public WaitlistResponseDto updateWaitlist(Long id, WaitlistUpdateDto waitlistUpdateDto, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update waitlists.");
        }

        Waitlist waitlist = waitlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Waitlist not found"));

        waitlist.setWaitlistLimit(waitlistUpdateDto.getCapacity());
        return ConvertToResponseDto.convertWaitlistToResponseDto(waitlistRepository.save(waitlist), waitlist.getCourse());
    }

    @Override
    public void deleteWaitlist(Long id, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete waitlists.");
        }

        if (!waitlistRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Waitlist not found");
        }
        waitlistRepository.deleteById(id);
    }
}
