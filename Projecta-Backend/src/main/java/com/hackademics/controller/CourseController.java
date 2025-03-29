package com.hackademics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.CourseDto;
import com.hackademics.dto.CourseResponseDto;
import com.hackademics.dto.CourseUpdateDto;
import com.hackademics.service.CourseService;

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
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "APIs for managing courses")
@SecurityRequirement(name = "bearer-jwt")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "Test endpoint", description = "Simple test endpoint to verify controller functionality")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Controller is working")
    })
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Course controller is working!");
    }

    @Operation(summary = "Get active courses", description = "Retrieves all currently active courses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active courses",
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class)))
    })
    @GetMapping("/active")
    public ResponseEntity<List<CourseResponseDto>> getAllActiveCourses() {
        return ResponseEntity.ok(courseService.getAllActiveCourses());
    }

    @Operation(summary = "Get upcoming courses", description = "Retrieves all courses scheduled for the next semester")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved upcoming courses",
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class)))
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<CourseResponseDto>> getAllUpcomingCourses() {
        return ResponseEntity.ok(courseService.getAllUpcomingCourses());
    }

    @Operation(summary = "Get admin's courses", description = "Retrieves all courses managed by a specific admin")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved admin's courses",
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping("/admin")
    public ResponseEntity<List<CourseResponseDto>> getAllCoursesByAdmin(
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(courseService.getAllCoursesByAdmin(currentUser));
    }

    @Operation(summary = "Get courses by subject", description = "Retrieves all courses for a specific subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved courses",
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @GetMapping("/subject/{id}")
    public ResponseEntity<List<CourseResponseDto>> getAllCoursesBySubjectId(
            @Parameter(description = "ID of the subject", required = true) 
            @PathVariable Long id) {
        return ResponseEntity.ok(courseService.getAllCoursesBySubjectId(id));
    }

    @Operation(summary = "Get course by ID", description = "Retrieves a specific course by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved course",
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(
            @Parameter(description = "ID of the course", required = true) 
            @PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Operation(summary = "Create course", description = "Creates a new course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created course",
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @Parameter(description = "Course data", required = true) 
            @Valid @RequestBody CourseDto courseDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(courseService.saveCourse(courseDto, currentUser));
    }

    @Operation(summary = "Update course", description = "Updates an existing course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated course",
                    content = @Content(schema = @Schema(implementation = CourseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @Parameter(description = "ID of the course to update", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Updated course data", required = true) 
            @RequestBody CourseUpdateDto courseUpdateDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseUpdateDto, currentUser));
    }

    @Operation(summary = "Delete course", description = "Deletes a course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted course"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "ID of the course to delete", required = true) 
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        courseService.deleteCourse(id, currentUser);
        return ResponseEntity.ok().build();
    }
}

