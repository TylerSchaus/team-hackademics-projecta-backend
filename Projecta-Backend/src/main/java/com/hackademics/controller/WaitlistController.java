package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hackademics.model.Waitlist;
import com.hackademics.service.WaitlistService;

import java.util.List;

@RestController
@RequestMapping("/api/waitlists")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    // Create a new waitlist
    @PostMapping
    public ResponseEntity<Waitlist> createWaitlist(@RequestBody Waitlist waitlist) {
        Waitlist savedWaitlist = waitlistService.saveWaitlist(waitlist);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWaitlist);
    }

    // Get all waitlists
    @GetMapping
    public ResponseEntity<List<Waitlist>> getAllWaitlists() {
        List<Waitlist> waitlists = waitlistService.getAllWaitlists();
        return ResponseEntity.ok(waitlists);
    }

    // Get a waitlist by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Waitlist> getWaitlistById(@PathVariable Long id) {
        Waitlist waitlist = waitlistService.getWaitlistById(id);
        return ResponseEntity.ok(waitlist);
    }

    // Update a waitlist
    @PutMapping("/{id}")
    public ResponseEntity<Waitlist> updateWaitlist(@PathVariable Long id, @RequestBody Waitlist waitlist) {
        waitlist.setId(id); // Ensure the ID is set for the update
        Waitlist updatedWaitlist = waitlistService.updateWaitlist(waitlist);
        return ResponseEntity.ok(updatedWaitlist);
    }

    // Delete a waitlist by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaitlist(@PathVariable Long id) {
        waitlistService.deleteWaitlist(id);
        return ResponseEntity.noContent().build();
    }
}
