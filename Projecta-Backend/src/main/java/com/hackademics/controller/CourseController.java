package com.hackademics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.hackademics.dto.CourseDto;
import com.hackademics.dto.CourseResponseDto;
import com.hackademics.dto.CourseUpdateDto;
import com.hackademics.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Course controller is working!");
    }

    /* Semi-essential */
    @GetMapping("/active") // Returns all courses that are current active. 
    public ResponseEntity<List<CourseResponseDto>> getAllActiveCourses() {
        return ResponseEntity.ok(courseService.getAllActiveCourses());
    }

    /* Essential */
    @GetMapping("/upcoming") // Returns all courses that are upcoming (i.e., next semester).
    public ResponseEntity<List<CourseResponseDto>> getAllUpcomingCourses() {
        return ResponseEntity.ok(courseService.getAllUpcomingCourses());
    }

    /* Semi-essential */ 
    @GetMapping("/admin") // Returns all courses for a specific admin.
    public ResponseEntity<List<CourseResponseDto>> getAllCoursesByAdmin(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(courseService.getAllCoursesByAdmin(currentUser));
    }

    /* Essential */
    @GetMapping("/subject/{id}") // Returns all courses for a specific subject.
    public ResponseEntity<List<CourseResponseDto>> getAllCoursesBySubjectId(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getAllCoursesBySubjectId(id));
    }

    /* Not essential */
    @GetMapping("/{id}")  
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    /* Less essential */
    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(@Valid @RequestBody CourseDto courseDto, @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(courseService.saveCourse(courseDto, currentUser));
    }

    /* Less essential */
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(@PathVariable Long id, @RequestBody CourseUpdateDto courseUpdateDto, @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseUpdateDto, currentUser));
    }

    /* Less essential */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        courseService.deleteCourse(id, currentUser);
        return ResponseEntity.ok().build();
    }
}

