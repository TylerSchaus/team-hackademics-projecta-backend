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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.RequestDto.SubjectDto;
import com.hackademics.dto.ResponseDto.SubjectResponseDto;
import com.hackademics.dto.UpdateDto.SubjectUpdateDto;
import com.hackademics.service.SubjectService;

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
@RequestMapping("/api/subjects")
@Tag(name = "Subjects", description = "APIs for managing subjects")
@SecurityRequirement(name = "bearer-jwt")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Operation(summary = "Get subject by ID", description = "Retrieves a specific subject by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved subject",
                    content = @Content(schema = @Schema(implementation = SubjectResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> getSubjectById(
            @Parameter(description = "ID of the subject", required = true) 
            @PathVariable Long id) {
        return subjectService.getSubjectById(id)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create subject", description = "Creates a new subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created subject",
                    content = @Content(schema = @Schema(implementation = SubjectResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PostMapping
    public ResponseEntity<SubjectResponseDto> createSubject(
            @Parameter(description = "Subject data", required = true) 
            @Valid @RequestBody SubjectDto subjectDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        SubjectResponseDto createdSubject = subjectService.createSubject(subjectDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
    }

    @Operation(summary = "Delete subject", description = "Deletes a subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted subject"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(
            @Parameter(description = "ID of the subject to delete", required = true) 
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        boolean deleted = subjectService.deleteSubject(id, currentUser);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get all subjects", description = "Retrieves all subjects")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved subjects",
                    content = @Content(schema = @Schema(implementation = SubjectResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<SubjectResponseDto>> getAllSubjects() {
        List<SubjectResponseDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @Operation(summary = "Update subject", description = "Updates an existing subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated subject",
                    content = @Content(schema = @Schema(implementation = SubjectResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Subject not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> updateSubject(
            @Parameter(description = "ID of the subject to update", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Updated subject data", required = true) 
            @Valid @RequestBody SubjectUpdateDto updatedSubjectDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        return subjectService.updateSubject(id, updatedSubjectDto, currentUser)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }
}