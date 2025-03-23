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

import com.hackademics.dto.LabSectionDto;
import com.hackademics.dto.LabSectionResponseDto;
import com.hackademics.dto.LabSectionUpdateDto;
import com.hackademics.service.LabSectionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lab-sections")
public class LabSectionController {

    @Autowired
    private LabSectionService labSectionService;

    @PostMapping
    public ResponseEntity<LabSectionResponseDto> createLabSection(@Valid @RequestBody LabSectionDto labSectionDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        LabSectionResponseDto createdLabSection = labSectionService.createLabSection(labSectionDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabSection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabSectionResponseDto> getLabSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(labSectionService.getLabSectionById(id));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LabSectionResponseDto>> getLabSectionsByCourseId(@PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(labSectionService.findByCourseId(courseId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabSectionResponseDto> updateLabSection(@PathVariable Long id,
            @Valid @RequestBody LabSectionUpdateDto labSectionUpdateDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        labSectionUpdateDto.setCourseId(id); // Ensure the ID matches the lab section being updated
        LabSectionResponseDto updatedLabSection = labSectionService.updateLabSection(labSectionUpdateDto, currentUser);
        return ResponseEntity.ok(updatedLabSection);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabSection(@PathVariable Long id) {
        labSectionService.deleteLabSection(id);
        return ResponseEntity.noContent().build();
    }
}
