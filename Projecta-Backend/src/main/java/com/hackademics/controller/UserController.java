package com.hackademics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.UserResponseDTO;
import com.hackademics.dto.UserUpdateDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.service.UserService;

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
@RequestMapping("/api/users")
@Tag(name = "Users", description = "APIs for managing users")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Get users by role", description = "Retrieves a list of users filtered by their role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(
            @Parameter(description = "Role to filter users by", required = true) @RequestParam Role role,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<UserResponseDTO> users = userService.getUsersByRole(role, currentUser);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user information",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Not authenticated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User user) {
            return userService.getUserByEmail(user.getUsername())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "Update user", description = "Updates a user's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable Long id,
            @Parameter(description = "User update data", required = true) @RequestBody @Valid UserUpdateDto userUpdateDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        UserResponseDTO updatedUser = userService.updateUser(id, userUpdateDto, currentUser);
        return ResponseEntity.ok(updatedUser);
    }
    
    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        userService.deleteUser(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
