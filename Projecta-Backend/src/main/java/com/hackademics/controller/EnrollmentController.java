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

import com.hackademics.dto.RequestDto.EnrollmentDto;
import com.hackademics.dto.ResponseDto.EnrollmentResponseDto;
import com.hackademics.service.EnrollmentService;

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
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "APIs for managing course enrollments")
@SecurityRequirement(name = "bearer-jwt")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Operation(summary = "Create enrollment", description = "Creates a new course enrollment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created enrollment",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Course or student not found")
    })
    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> saveEnrollment(
            @Parameter(description = "Enrollment data", required = true) 
            @Valid @RequestBody EnrollmentDto enrollmentDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        EnrollmentResponseDto createdEnrollment = enrollmentService.saveEnrollment(enrollmentDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEnrollment);
    }

    @Operation(summary = "Get all enrollments", description = "Retrieves all enrollments for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved enrollments",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    })
    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDto>> getAllEnrollments(
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments(currentUser));
    }

    @Operation(summary = "Get enrollments by course", description = "Retrieves all enrollments for a specific course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved enrollments",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDto>> getEnrollmentsByCourseId(
            @Parameter(description = "ID of the course", required = true) 
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourseId(courseId, currentUser));
    }

    @Operation(summary = "Get enrollments by student", description = "Retrieves all enrollments for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved enrollments",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponseDto>> getEnrollmentsByStudentId(
            @Parameter(description = "ID of the student", required = true) 
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudentId(studentId, currentUser));
    }

    @Operation(summary = "Get enrollment by ID", description = "Retrieves a specific enrollment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved enrollment",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDto> getEnrollmentById(
            @Parameter(description = "ID of the enrollment", required = true) 
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id, currentUser));
    }

    @Operation(summary = "Delete enrollment", description = "Deletes a course enrollment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted enrollment"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(
            @Parameter(description = "ID of the enrollment to delete", required = true) 
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        enrollmentService.deleteEnrollment(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get current enrollments", description = "Retrieves current enrollments for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved current enrollments",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/student/{studentId}/current")
    public ResponseEntity<List<EnrollmentResponseDto>> getCurrentEnrollmentsByStudentId(
            @Parameter(description = "ID of the student", required = true) 
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<EnrollmentResponseDto> currentEnrollments = enrollmentService.getCurrentEnrollmentByStudentId(currentUser, studentId, null);
        return ResponseEntity.ok(currentEnrollments);
    }

    @Operation(summary = "Get enrollments by term", description = "Retrieves enrollments for a specific student in a given term")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved enrollments",
                    content = @Content(schema = @Schema(implementation = EnrollmentResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/student/{studentId}/{term}")
    public ResponseEntity<List<EnrollmentResponseDto>> getCurrentEnrollmentsByStudentId(
            @Parameter(description = "ID of the student", required = true) 
            @PathVariable Long studentId,
            @Parameter(description = "Term to filter enrollments", required = true) 
            @PathVariable String term,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<EnrollmentResponseDto> currentEnrollments = enrollmentService.getCurrentEnrollmentByStudentId(currentUser, studentId, term);
        return ResponseEntity.ok(currentEnrollments);
    }
}
