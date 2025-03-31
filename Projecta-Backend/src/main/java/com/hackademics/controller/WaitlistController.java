package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.hackademics.dto.WaitlistDto;
import com.hackademics.dto.WaitlistUpdateDto;
import com.hackademics.dto.WaitlistResponseDto;
import com.hackademics.service.WaitlistService;

import java.util.List;

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
@RequestMapping("/api/waitlists")
@Tag(name = "Waitlists", description = "APIs for managing course waitlists")
@SecurityRequirement(name = "bearer-jwt")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    @Operation(summary = "Create waitlist", description = "Creates a new waitlist for a course (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created waitlist",
                    content = @Content(schema = @Schema(implementation = WaitlistResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PostMapping
    public ResponseEntity<WaitlistResponseDto> createWaitlist(
            @Parameter(description = "Waitlist data", required = true) 
            @Valid @RequestBody WaitlistDto waitlistDto, 
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(waitlistService.saveWaitlist(waitlistDto, currentUser));
    }

    @Operation(summary = "Get all waitlists", description = "Retrieves all waitlists (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved waitlists",
                    content = @Content(schema = @Schema(implementation = WaitlistResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping
    public ResponseEntity<List<WaitlistResponseDto>> getAllWaitlists(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(waitlistService.getAllWaitlists(currentUser));
    }

    @Operation(summary = "Get waitlist by ID", description = "Retrieves a specific waitlist by its ID (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved waitlist",
                    content = @Content(schema = @Schema(implementation = WaitlistResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Waitlist not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<WaitlistResponseDto> getWaitlistById(
            @Parameter(description = "ID of the waitlist", required = true) 
            @PathVariable Long id, 
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(waitlistService.getWaitlistById(id, currentUser));
    }

    @Operation(summary = "Update waitlist", description = "Updates an existing waitlist (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated waitlist",
                    content = @Content(schema = @Schema(implementation = WaitlistResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Waitlist not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<WaitlistResponseDto> updateWaitlist(
            @Parameter(description = "ID of the waitlist to update", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Updated waitlist data", required = true) 
            @Valid @RequestBody WaitlistUpdateDto waitlistUpdateDto, 
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(waitlistService.updateWaitlist(id, waitlistUpdateDto, currentUser));
    }

    @Operation(summary = "Delete waitlist", description = "Deletes a waitlist (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted waitlist"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Waitlist not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlist(
            @Parameter(description = "ID of the waitlist to delete", required = true) 
            @PathVariable Long id, 
            @AuthenticationPrincipal UserDetails currentUser) {
        waitlistService.deleteWaitlist(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}