package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.hackademics.model.Waitlist;
import com.hackademics.service.WaitlistService;

import java.util.List;

@RestController
@RequestMapping("/api/waitlists")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    // Create a new waitlist (Admin only)
    @PostMapping
    public ResponseEntity<Waitlist> createWaitlist(@RequestBody Waitlist waitlist, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Waitlist savedWaitlist = waitlistService.saveWaitlist(waitlist);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWaitlist);
    }

    // Get all waitlists (Admin only)
    @GetMapping
    public ResponseEntity<List<Waitlist>> getAllWaitlists(@AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Waitlist> waitlists = waitlistService.getAllWaitlists();
        return ResponseEntity.ok(waitlists);
    }

    // Get a waitlist by its ID (Admin only)
    @GetMapping("/{id}")
    public ResponseEntity<Waitlist> getWaitlistById(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Waitlist waitlist = waitlistService.getWaitlistById(id);
        return ResponseEntity.ok(waitlist);
    }

    // Update a waitlist (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<Waitlist> updateWaitlist(@PathVariable Long id, @RequestBody Waitlist waitlist, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        waitlist.setId(id); // Ensure the ID is set for the update
        Waitlist updatedWaitlist = waitlistService.updateWaitlist(waitlist);
        return ResponseEntity.ok(updatedWaitlist);
    }

    // Delete a waitlist by its ID (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlist(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        waitlistService.deleteWaitlist(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to check if the current user is an admin
    private boolean isAdmin(UserDetails currentUser) {
        return currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}