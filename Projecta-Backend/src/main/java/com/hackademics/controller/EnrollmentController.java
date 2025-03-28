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
import com.hackademics.dto.EnrollmentResponseDto;
import com.hackademics.service.EnrollmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    /* Essential */
    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> saveEnrollment(@Valid @RequestBody EnrollmentDto enrollmentDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        EnrollmentResponseDto createdEnrollment = enrollmentService.saveEnrollment(enrollmentDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEnrollment);
    }

    /* Less essential */
    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDto>> getAllEnrollments(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments(currentUser));
    }

    /* Semi essential */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDto>> getEnrollmentsByCourseId(@PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourseId(courseId, currentUser));
    }

    /* Essential */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponseDto>> getEnrollmentsByStudentId(@PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudentId(studentId, currentUser));
    }

    /* Not essential */
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDto> getEnrollmentById(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id, currentUser));
    }

    /* Essential */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        enrollmentService.deleteEnrollment(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    /* Essential */
    @GetMapping("/student/{studentId}/current")
    public ResponseEntity<List<EnrollmentResponseDto>> getCurrentEnrollmentsByStudentId(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<EnrollmentResponseDto> currentEnrollments = enrollmentService.getCurrentEnrollmentByStudentId(currentUser, studentId, null);
        return ResponseEntity.ok(currentEnrollments);
    }

    /* Essential */
    @GetMapping("/student/{studentId}/{term}")
    public ResponseEntity<List<EnrollmentResponseDto>> getCurrentEnrollmentsByStudentId(
            @PathVariable Long studentId,
            @PathVariable String term,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<EnrollmentResponseDto> currentEnrollments = enrollmentService.getCurrentEnrollmentByStudentId(currentUser, studentId, term);
        return ResponseEntity.ok(currentEnrollments);
    }
}
