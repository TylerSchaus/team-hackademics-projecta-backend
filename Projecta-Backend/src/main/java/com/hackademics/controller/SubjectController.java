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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.SubjectDto;
import com.hackademics.model.Subject;
import com.hackademics.service.SubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build()); // Ensures 404 instead of throwing an exception
    }

    @PostMapping
    public ResponseEntity<Subject> createSubject(@Valid @RequestBody SubjectDto subjectDto, @AuthenticationPrincipal UserDetails currentUser) {
        Subject createdSubject = subjectService.createSubject(subjectDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        boolean deleted = subjectService.deleteSubject(id, currentUser);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

     @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectDto updatedSubjectDto, @AuthenticationPrincipal UserDetails currentUser) {
        return subjectService.updateSubject(id, updatedSubjectDto, currentUser)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }
}