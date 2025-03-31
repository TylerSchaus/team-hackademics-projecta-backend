package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.CourseDto;
import com.hackademics.dto.CourseResponseDto;
import com.hackademics.dto.CourseUpdateDto;
import com.hackademics.model.Course;

public interface CourseService {
    CourseResponseDto convertToResponseDto(Course course);
    CourseResponseDto saveCourse(CourseDto courseDto, UserDetails currentUser);
    List<CourseResponseDto> getAllActiveCourses();
    List<CourseResponseDto> getAllUpcomingCourses();
    List<CourseResponseDto> getAllCoursesByAdmin(UserDetails currentUser);
    List<CourseResponseDto> getAllCoursesBySubjectId(Long id);
    CourseResponseDto getCourseById(Long id);
    CourseResponseDto updateCourse(Long id, CourseUpdateDto courseUpdateDto, UserDetails currentUser);
    void deleteCourse(Long id, UserDetails currentUser);
}