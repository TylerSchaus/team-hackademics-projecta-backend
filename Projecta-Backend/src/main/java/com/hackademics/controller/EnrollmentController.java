package com.hackademics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.EnrollmentDto;
import com.hackademics.model.Enrollment;
import com.hackademics.service.EnrollmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<Enrollment> saveEnrollment(@Valid @RequestBody EnrollmentDto enrollmentDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        Enrollment createdEnrollment = enrollmentService.saveEnrollment(enrollmentDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEnrollment);
    }

    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments(currentUser));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourseId(@PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourseId(courseId, currentUser));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudentId(@PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudentId(studentId, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        enrollmentService.deleteEnrollment(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}/current")
    public ResponseEntity<List<Enrollment>> getCurrentEnrollmentsByStudentId(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<Enrollment> currentEnrollments = enrollmentService.getCurrentEnrollmentByStudentId(currentUser, studentId);
        return ResponseEntity.ok(currentEnrollments);
    }
}
