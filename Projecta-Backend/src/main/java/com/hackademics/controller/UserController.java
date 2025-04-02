package com.hackademics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.RequestDto.AdminSignUpDto;
import com.hackademics.dto.ResponseDto.UserResponseDTO;
import com.hackademics.dto.UpdateDto.UserUpdateDto;
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

    @Operation(summary = "Get students by grade range", description = "Retrieves students whose grades fall within the specified range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved students",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping("/grade-range")
    public ResponseEntity<List<UserResponseDTO>> getStudentsByGradeRange(
            @Parameter(description = "Lower bound of grade range", required = true) 
            @RequestParam double low,
            @Parameter(description = "Upper bound of grade range", required = true) 
            @RequestParam double high,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(userService.getStudentsByGradeRange(low, high, currentUser));
    }

    @Operation(summary = "Get students by name prefix", description = "Retrieves students whose first names start with the specified prefix")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved students",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping("/name-prefix")
    public ResponseEntity<List<UserResponseDTO>> getStudentsByNamePrefix(
            @Parameter(description = "Prefix to search for in student names", required = true) 
            @RequestParam String prefix,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(userService.getStudentsByNamePrefix(prefix, currentUser));
    }

    @Operation(summary = "Register new user from admin portal", description = "Registers a new user in the system from the admin portal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully registered user",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or email already in use"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/admin-signup")
    public ResponseEntity<?> register(
            @Parameter(description = "User registration data", required = true) 
            @Valid @RequestBody AdminSignUpDto signUpDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        try {
            UserResponseDTO registeredUser = userService.signupUserFromAdminPortal(signUpDto, currentUser);
            return ResponseEntity.ok(registeredUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email is already in use");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the user");
        }
    }
    
}
