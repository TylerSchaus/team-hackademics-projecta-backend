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

import com.hackademics.dto.WaitlistEnrollmentDto;
import com.hackademics.dto.WaitlistEnrollmentResponseDto;
import com.hackademics.service.WaitlistEnrollmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/waitlist-enrollments")
@Tag(name = "Waitlist Enrollments", description = "APIs for managing waitlist enrollments")
@SecurityRequirement(name = "bearer-jwt")
public class WaitlistEnrollmentController {

    @Autowired
    private WaitlistEnrollmentService waitlistEnrollmentService;

    @Operation(summary = "Create waitlist enrollment", description = "Creates a new waitlist enrollment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created waitlist enrollment",
                    content = @Content(schema = @Schema(implementation = WaitlistEnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PostMapping
    public ResponseEntity<WaitlistEnrollmentResponseDto> createWaitlistEnrollment(
            @Parameter(description = "Waitlist enrollment data", required = true) 
            @Valid @RequestBody WaitlistEnrollmentDto waitlistEnrollmentDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(waitlistEnrollmentService.saveWaitlistEnrollment(waitlistEnrollmentDto, currentUser));
    }


    @Operation(summary = "Delete waitlist enrollment", description = "Deletes a waitlist enrollment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted waitlist enrollment"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Waitlist enrollment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlistEnrollment(
            @Parameter(description = "ID of the waitlist enrollment to delete", required = true) 
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        waitlistEnrollmentService.deleteWaitlistEnrollment(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get waitlist enrollments by student", description = "Retrieves all waitlist enrollments for a specific student (admin or the student themselves only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved course waitlist enrollments",
                    content = @Content(schema = @Schema(implementation = WaitlistEnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<WaitlistEnrollmentResponseDto>> getWaitlistEnrollmentsByStudentId(
            @Parameter(description = "ID of the student", required = true) 
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(waitlistEnrollmentService.getWaitlistEnrollmentsByStudentId(studentId, currentUser));
    }

     /* private void updateWaitlistPositions(Long waitlistId) {
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
    } */
}