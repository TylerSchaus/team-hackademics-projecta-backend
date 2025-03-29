package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.WaitlistEnrollmentService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/waitlist-enrollments")
public class WaitlistEnrollmentController {

    @Autowired
    private WaitlistEnrollmentService waitlistEnrollmentService;
    
    @Autowired
    private WaitlistRepository waitlistRepository;

    @PostMapping
    public ResponseEntity<WaitlistEnrollment> createWaitlistEnrollment(
            @RequestBody WaitlistEnrollment waitlistEnrollment,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        // Authorization check
        if (!isAdmin(currentUser) && !isStudent(currentUser, waitlistEnrollment.getStudent().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Get the waitlist
        Waitlist waitlist = waitlistRepository.findById(waitlistEnrollment.getWaitlist().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Waitlist not found"));
        
        // Count current enrollments by filtering all enrollments
        List<WaitlistEnrollment> allEnrollments = waitlistEnrollmentService.getAllWaitlistEnrollments();
        long currentEnrollments = allEnrollments.stream()
                .filter(e -> e.getWaitlist().getId().equals(waitlist.getId()))
                .count();
        
        // Check capacity against waitlist limit
        if (currentEnrollments >= waitlist.getWaitlistLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Waitlist is at capacity. Cannot add more enrollments.");
        }
        
        // Set position and create
        waitlistEnrollment.setWaitlistPosition((int)currentEnrollments + 1);
        WaitlistEnrollment createdEnrollment = waitlistEnrollmentService.saveWaitlistEnrollment(waitlistEnrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEnrollment);
    }

    @GetMapping
    public ResponseEntity<List<WaitlistEnrollment>> getAllWaitlistEnrollments(
            @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(waitlistEnrollmentService.getAllWaitlistEnrollments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaitlistEnrollment> getWaitlistEnrollmentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(waitlistEnrollmentService.getWaitlistEnrollmentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlistEnrollment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        WaitlistEnrollment enrollment = waitlistEnrollmentService.getWaitlistEnrollmentById(id);
        
        // Authorization check
        if (!isAdmin(currentUser) && !isStudent(currentUser, enrollment.getStudent().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Delete the enrollment
        waitlistEnrollmentService.deleteWaitlistEnrollment(id);
        
        // Update positions of remaining enrollments
        updateWaitlistPositions(enrollment.getWaitlist().getId());
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<WaitlistEnrollment>> getWaitlistEnrollmentsByCourseId(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<WaitlistEnrollment> allEnrollments = waitlistEnrollmentService.getAllWaitlistEnrollments();
        List<WaitlistEnrollment> courseEnrollments = allEnrollments.stream()
                .filter(e -> e.getWaitlist().getCourse().getId().equals(courseId))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(courseEnrollments);
    }

    private void updateWaitlistPositions(Long waitlistId) {
        List<WaitlistEnrollment> enrollments = waitlistEnrollmentService.getAllWaitlistEnrollments().stream()
                .filter(e -> e.getWaitlist().getId().equals(waitlistId))
                .sorted(Comparator.comparingInt(WaitlistEnrollment::getWaitlistPosition))
                .collect(Collectors.toList());
        
        for (int i = 0; i < enrollments.size(); i++) {
            WaitlistEnrollment enrollment = enrollments.get(i);
            enrollment.setWaitlistPosition(i + 1);
            waitlistEnrollmentService.saveWaitlistEnrollment(enrollment);
        }
    }

    private boolean isAdmin(UserDetails currentUser) {
        return currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isStudent(UserDetails currentUser, Long studentId) {
        return currentUser.getUsername().equals(studentId.toString());
    }
}