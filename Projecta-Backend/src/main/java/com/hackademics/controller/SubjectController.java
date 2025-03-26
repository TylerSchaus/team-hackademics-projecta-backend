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

import com.hackademics.dto.SubjectDto;
import com.hackademics.dto.SubjectResponseDto;
import com.hackademics.dto.SubjectUpdateDto;
import com.hackademics.service.SubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /* Not essential */
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }

    /* Not essential */
    @PostMapping
    public ResponseEntity<SubjectResponseDto> createSubject(@Valid @RequestBody SubjectDto subjectDto, @AuthenticationPrincipal UserDetails currentUser) {
        SubjectResponseDto createdSubject = subjectService.createSubject(subjectDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
    }

    /* Not essential */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        boolean deleted = subjectService.deleteSubject(id, currentUser);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /* Essential */
    @GetMapping
    public ResponseEntity<List<SubjectResponseDto>> getAllSubjects() {
        List<SubjectResponseDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    /* Not essential */
    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponseDto> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectUpdateDto updatedSubjectDto, @AuthenticationPrincipal UserDetails currentUser) {
        return subjectService.updateSubject(id, updatedSubjectDto, currentUser)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
    }
}