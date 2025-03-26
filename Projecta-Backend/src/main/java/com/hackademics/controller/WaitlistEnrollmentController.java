package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.model.User;
import com.hackademics.service.WaitlistEnrollmentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/waitlist-enrollments")
public class WaitlistEnrollmentController {

    @Autowired
    private WaitlistEnrollmentService waitlistEnrollmentService;

    // Create a new waitlist enrollment (Admin for any student, student for themselves)
    @PostMapping
    public ResponseEntity<WaitlistEnrollment> createWaitlistEnrollment(
            @RequestBody WaitlistEnrollment waitlistEnrollment,
            @AuthenticationPrincipal UserDetails currentUser) {

        // Check if the waitlist is at capacity
        if (waitlistEnrollment.getWaitlist().getCurrentCapacity() >= waitlistEnrollment.getWaitlist().getMaxCapacity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Waitlist is full
        }

        // Check user permissions
        if (!isAdmin(currentUser) && !isStudent(currentUser, waitlistEnrollment.getStudent().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Save enrollment
        WaitlistEnrollment savedEnrollment = waitlistEnrollmentService.saveWaitlistEnrollment(waitlistEnrollment);

       // Add the new enrollment to the waitlist's enrollments (this will update the current capacity)
       waitlistEnrollment.getWaitlist().getWaitlistEnrollments().add(savedEnrollment);

       // Update the waitlist with the new enrollment
       waitlistEnrollmentService.updateWaitlistEnrollment(waitlistEnrollment.getWaitlist());

       return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollment);
   }

    // Delete a waitlist enrollment by its ID (Admin for all students, student for themselves)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlistEnrollment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {

        WaitlistEnrollment enrollment = waitlistEnrollmentService.getWaitlistEnrollmentById(id);
        if (enrollment == null) {
            return ResponseEntity.notFound().build();
        }

        // Check user permissions
        if (!isAdmin(currentUser) && !isStudent(currentUser, enrollment.getStudent().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Remove the enrollment from the waitlist
        enrollment.getWaitlist().getWaitlistEnrollments().remove(enrollment);

        // Update the waitlist with the new enrollment list (this will update the current capacity)
        waitlistEnrollmentService.updateWaitlistEnrollment(enrollment.getWaitlist());

        // Delete the waitlist enrollment
        waitlistEnrollmentService.deleteWaitlistEnrollment(id);

        return ResponseEntity.noContent().build();
    }

    // Get all waitlist enrollments for a specific course (Admin only)
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<WaitlistEnrollment>> getWaitlistEnrollmentsByCourseId(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {

        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<WaitlistEnrollment> allEnrollments = waitlistEnrollmentService.getAllWaitlistEnrollments();
        List<WaitlistEnrollment> courseEnrollments = allEnrollments.stream()
                .filter(enrollment -> enrollment.getWaitlist().getCourse().getId().equals(courseId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(courseEnrollments);
    }

    // Helper method to check if the current user is an admin
    private boolean isAdmin(UserDetails currentUser) {
        return currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    // Helper method to check if the current user is the student themselves
    private boolean isStudent(UserDetails currentUser, Long studentId) {
        return currentUser.getUsername().equals(studentId.toString());
    }
}

