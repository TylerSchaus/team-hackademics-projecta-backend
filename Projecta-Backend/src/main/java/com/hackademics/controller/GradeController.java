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

import com.hackademics.dto.RequestDto.GradeDto;
import com.hackademics.dto.ResponseDto.GradeResponseDto;
import com.hackademics.dto.UpdateDto.GradeUpdateDto;
import com.hackademics.service.GradeService;

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
@RequestMapping("/api/grades")
@Tag(name = "Grades", description = "APIs for managing grades")
@SecurityRequirement(name = "bearer-jwt")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Operation(summary = "Create grade", description = "Creates a new grade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created grade",
                    content = @Content(schema = @Schema(implementation = GradeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PostMapping
    public ResponseEntity<GradeResponseDto> createGrade(
            @Parameter(description = "Grade data", required = true) 
            @Valid @RequestBody GradeDto gradeDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(gradeService.saveGrade(gradeDto, currentUser));
    }

    @Operation(summary = "Get all grades", description = "Retrieves all grades (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved grades",
                    content = @Content(schema = @Schema(implementation = GradeResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping
    public ResponseEntity<List<GradeResponseDto>> getAllGrades(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(gradeService.getAllGrades(currentUser));
    }

    @Operation(summary = "Get grade by ID", description = "Retrieves a specific grade by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved grade",
                    content = @Content(schema = @Schema(implementation = GradeResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Grade not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GradeResponseDto> getGradeById(
            @Parameter(description = "ID of the grade", required = true) 
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(gradeService.getGradeById(id, currentUser));
    }

    @Operation(summary = "Update grade", description = "Updates an existing grade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated grade",
                    content = @Content(schema = @Schema(implementation = GradeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Grade not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GradeResponseDto> updateGrade(
            @Parameter(description = "ID of the grade to update", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Updated grade data", required = true) 
            @Valid @RequestBody GradeUpdateDto gradeUpdateDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(gradeService.updateGrade(id, gradeUpdateDto, currentUser));
    }

    @Operation(summary = "Delete grade", description = "Deletes a grade")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted grade"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Grade not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(
            @Parameter(description = "ID of the grade to delete", required = true) 
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        gradeService.deleteGrade(id, currentUser);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get student grades", description = "Retrieves all grades for a specific student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved student grades",
                    content = @Content(schema = @Schema(implementation = GradeResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeResponseDto>> getGradesByStudentId(
            @Parameter(description = "ID of the student", required = true) 
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId, currentUser));
    }
}
