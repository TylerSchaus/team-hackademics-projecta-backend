package com.hackademics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.RequestDto.LabSectionDto;
import com.hackademics.dto.ResponseDto.LabSectionResponseDto;
import com.hackademics.service.LabSectionService;

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
@RequestMapping("/api/lab-sections")
@Tag(name = "Lab Sections", description = "APIs for managing lab sections")
@SecurityRequirement(name = "bearer-jwt")
public class LabSectionController {

    @Autowired
    private LabSectionService labSectionService;

    @Operation(summary = "Create lab section", description = "Creates a new lab section")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created lab section",
                    content = @Content(schema = @Schema(implementation = LabSectionResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PostMapping
    public ResponseEntity<LabSectionResponseDto> createLabSection(
            @Parameter(description = "Lab section data", required = true) 
            @Valid @RequestBody LabSectionDto labSectionDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        LabSectionResponseDto createdLabSection = labSectionService.createLabSection(labSectionDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabSection);
    }

    @Operation(summary = "Get lab section by ID", description = "Retrieves a specific lab section by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved lab section",
                    content = @Content(schema = @Schema(implementation = LabSectionResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Lab section not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LabSectionResponseDto> getLabSectionById(
            @Parameter(description = "ID of the lab section", required = true) 
            @PathVariable Long id) {
        return ResponseEntity.ok(labSectionService.getLabSectionById(id));
    }

    @Operation(summary = "Get lab sections by course", description = "Retrieves all lab sections for a specific course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved lab sections",
                    content = @Content(schema = @Schema(implementation = LabSectionResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LabSectionResponseDto>> getLabSectionsByCourseId(
            @Parameter(description = "ID of the course", required = true) 
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(labSectionService.findByCourseId(courseId));
    }

}
