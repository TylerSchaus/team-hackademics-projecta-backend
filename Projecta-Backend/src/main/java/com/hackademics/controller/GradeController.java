package com.hackademics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.hackademics.model.Grade;
import com.hackademics.model.User;
import com.hackademics.service.GradeService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    // Create a new grade (Admin only)
    @PostMapping
    public ResponseEntity<Grade> createGrade(@RequestBody Grade grade, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Grade savedGrade = gradeService.saveGrade(grade);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGrade);
    }

    // Get all grades (Admin only)
    @GetMapping
    public ResponseEntity<List<Grade>> getAllGrades(@AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Grade> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(grades);
    }

    // Get a grade by its ID (Admin only)
    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Grade grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(grade);
    }

    // Update a grade (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<Grade> updateGrade(@PathVariable Long id, @RequestBody Grade grade, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        grade.setId(id); // Ensure the ID is set for the update
        Grade updatedGrade = gradeService.updateGrade(grade);
        return ResponseEntity.ok(updatedGrade);
    }

    // Delete a grade by its ID (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    // Get all grades for a specific user (Admin or student for their own grades)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Grade>> getGradesByStudentId(@PathVariable Long studentId, @AuthenticationPrincipal UserDetails currentUser) {
        if (!isAdmin(currentUser) && !isStudent(currentUser, studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Fetch all grades and filter by student ID
        List<Grade> allGrades = gradeService.getAllGrades();
        List<Grade> studentGrades = allGrades.stream()
                .filter(grade -> grade.getStudent().getId().equals(studentId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentGrades);
    }

    // Helper method to check if the current user is an admin
    private boolean isAdmin(UserDetails currentUser) {
        return currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    // Helper method to check if the current user is the student themselves
    private boolean isStudent(UserDetails currentUser, Long studentId) {
        return currentUser.getUsername().equals(studentId.toString());
    }
}