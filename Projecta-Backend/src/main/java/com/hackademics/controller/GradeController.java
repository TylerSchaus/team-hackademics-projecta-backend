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

import com.hackademics.model.Grade;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.GradeService;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Grade> createGrade(@RequestBody Grade grade, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        grade.setStudentId(grade.getStudent().getStudentId());
        return ResponseEntity.ok(gradeService.saveGrade(grade));
    }

    @GetMapping
    public ResponseEntity<List<Grade>> getAllGrades(@AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grade> updateGrade(@PathVariable Long id, @RequestBody Grade grade, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        grade.setId(id);
        grade.setStudentId(grade.getStudent().getStudentId());
        return ResponseEntity.ok(gradeService.updateGrade(grade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Grade>> getGradesByStudentId(@PathVariable Long studentId, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN && !user.getId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Grade> allGrades = gradeService.getAllGrades();
        List<Grade> studentGrades = allGrades.stream()
                .filter(grade -> grade.getStudent().getId().equals(studentId))
                .toList();

        return ResponseEntity.ok(studentGrades);
    }
}