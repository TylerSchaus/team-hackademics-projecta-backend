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

import com.hackademics.dto.CourseDto;
import com.hackademics.dto.CourseUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/active")
    public ResponseEntity<List<Course>> getAllActiveCourses() {
        return ResponseEntity.ok(courseService.getAllActiveCourses());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Course>> getAllUpcomingCourses() {
        return ResponseEntity.ok(courseService.getAllUpcomingCourses());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Course>> getAllCoursesByAdmin(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(courseService.getAllCoursesByAdmin(currentUser));
    }


    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Course>> getAllCoursesBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(courseService.getAllCoursesBySubjectId(subjectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseDto courseDto, @AuthenticationPrincipal UserDetails currentUser) {
        Course createdCourse = courseService.saveCourse(courseDto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateDto courseUpdateDto, @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseUpdateDto, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentUser) {
        courseService.deleteCourse(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}

