package com.hackademics.controller;

import java.util.List;

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

import com.hackademics.dto.UserUpdateDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsersByRole(
            @RequestParam Role role,
            @AuthenticationPrincipal UserDetails currentUser) {

        // Delegate the request to the service layer
        List<User> users = userService.getUsersByRole(role, currentUser);
        
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User user) {
            return userService.getUserByEmail(user.getUsername())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDto userUpdateDto,
            @AuthenticationPrincipal UserDetails currentUser) {
    
        // Delegate the update process to the service layer
        User updatedUser = userService.updateUser(id, userUpdateDto, currentUser);
    
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
    
        // Delegate role verification and deletion to service
        userService.deleteUser(id, currentUser);
    
        return ResponseEntity.noContent().build();
    }
    

}
