package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.service.WaitlistEnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/api/waitlist-enrollments")
public class WaitlistEnrollmentController {

    @Autowired
    private WaitlistEnrollmentService waitlistEnrollmentService;

    // Create a new waitlist enrollment
    @PostMapping
    public ResponseEntity<WaitlistEnrollment> createWaitlistEnrollment(@RequestBody WaitlistEnrollment waitlistEnrollment) {
        WaitlistEnrollment savedEnrollment = waitlistEnrollmentService.saveWaitlistEnrollment(waitlistEnrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollment);
    }

    // Get all waitlist enrollments
    @GetMapping
    public ResponseEntity<List<WaitlistEnrollment>> getAllWaitlistEnrollments() {
        List<WaitlistEnrollment> enrollments = waitlistEnrollmentService.getAllWaitlistEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    // Get a waitlist enrollment by its ID
    @GetMapping("/{id}")
    public ResponseEntity<WaitlistEnrollment> getWaitlistEnrollmentById(@PathVariable Long id) {
        WaitlistEnrollment enrollment = waitlistEnrollmentService.getWaitlistEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    // Update a waitlist enrollment
    @PutMapping("/{id}")
    public ResponseEntity<WaitlistEnrollment> updateWaitlistEnrollment(@PathVariable Long id, @RequestBody WaitlistEnrollment waitlistEnrollment) {
        waitlistEnrollment.setId(id); // Ensure the ID is set for the update
        WaitlistEnrollment updatedEnrollment = waitlistEnrollmentService.updateWaitlistEnrollment(waitlistEnrollment);
        return ResponseEntity.ok(updatedEnrollment);
    }

    // Delete a waitlist enrollment by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlistEnrollment(@PathVariable Long id) {
        waitlistEnrollmentService.deleteWaitlistEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
