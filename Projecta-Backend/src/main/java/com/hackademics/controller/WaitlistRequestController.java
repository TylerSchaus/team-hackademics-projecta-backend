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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.RequestDto.WaitlistRequestDto;
import com.hackademics.dto.ResponseDto.WaitlistRequestResponseDto;
import com.hackademics.service.WaitlistRequestService;

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
@RequestMapping("/api/waitlist-requests")
@Tag(name = "Waitlist Request", description = "APIs for managing waitlist requests")
@SecurityRequirement(name = "bearerAuth")
public class WaitlistRequestController {

    @Autowired
    private WaitlistRequestService waitlistRequestService;

    @PostMapping
    @Operation(summary = "Create a new waitlist request", description = "Creates a new waitlist request for a student")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Waitlist request created successfully",
            content = @Content(schema = @Schema(implementation = WaitlistRequestResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only admins or the student themselves can create requests"),
        @ApiResponse(responseCode = "404", description = "Waitlist or student not found")
    })
    public ResponseEntity<WaitlistRequestResponseDto> createWaitlistRequest(
            @Valid @RequestBody WaitlistRequestDto waitlistRequestDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(waitlistRequestService.saveWaitlistRequest(waitlistRequestDto, currentUser));
    }

    @GetMapping
    @Operation(summary = "Get all waitlist requests", description = "Retrieves all waitlist requests (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all waitlist requests",
            content = @Content(schema = @Schema(implementation = WaitlistRequestResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only admins can view all requests")
    })
    public ResponseEntity<List<WaitlistRequestResponseDto>> getAllWaitlistRequests(
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(waitlistRequestService.getAllWaitlistRequests(currentUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a waitlist request", description = "Deletes a specific waitlist request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Waitlist request deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only admins or the student themselves can delete requests"),
        @ApiResponse(responseCode = "404", description = "Waitlist request not found")
    })
    public ResponseEntity<Void> deleteWaitlistRequest(
            @Parameter(description = "ID of the waitlist request to delete") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        waitlistRequestService.deleteWaitlistRequest(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
