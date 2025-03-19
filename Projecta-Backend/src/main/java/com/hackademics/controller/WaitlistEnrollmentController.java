package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.hackademics.model.WaitlistEnrollment;
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
        // Check if the current user is an admin or the student themselves
        if (!isAdmin(currentUser) && !isStudent(currentUser, waitlistEnrollment.getStudent().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        WaitlistEnrollment savedEnrollment = waitlistEnrollmentService.saveWaitlistEnrollment(waitlistEnrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollment);
    }

    // Get all waitlist enrollments (Admin only)
    @GetMapping
    public ResponseEntity<List<WaitlistEnrollment>> getAllWaitlistEnrollments(@AuthenticationPrincipal UserDetails currentUser) {
        // Check if the current user is an admin
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<WaitlistEnrollment> enrollments = waitlistEnrollmentService.getAllWaitlistEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    // Get a waitlist enrollment by its ID (Admin only)
    @GetMapping("/{id}")
    public ResponseEntity<WaitlistEnrollment> getWaitlistEnrollmentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        // Check if the current user is an admin
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        WaitlistEnrollment enrollment = waitlistEnrollmentService.getWaitlistEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    // Delete a waitlist enrollment by its ID (Admin for all students, student for themselves)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlistEnrollment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        // Fetch the enrollment to check the student ID
        WaitlistEnrollment enrollment = waitlistEnrollmentService.getWaitlistEnrollmentById(id);
        if (enrollment == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if the current user is an admin or the student themselves
        if (!isAdmin(currentUser) && !isStudent(currentUser, enrollment.getStudent().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        waitlistEnrollmentService.deleteWaitlistEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    // Get all waitlist enrollments for a specific course (Admin only)
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<WaitlistEnrollment>> getWaitlistEnrollmentsByCourseId(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {
        // Check if the current user is an admin
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Fetch all enrollments and filter by course ID
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