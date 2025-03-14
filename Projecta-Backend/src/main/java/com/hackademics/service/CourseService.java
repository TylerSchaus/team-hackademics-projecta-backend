package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.CourseDto;
import com.hackademics.dto.CourseUpdateDto;
import com.hackademics.model.Course;

public interface CourseService {
    Course saveCourse(CourseDto courseDto, UserDetails currentUser);
    List<Course> getAllActiveCourses(); 
    List<Course> getAllUpcomingCourses();
    List<Course> getAllCoursesByAdmin(UserDetails currentUser);
    List<Course> getAllCoursesBySubjectId(Long id);
    Course getCourseById(Long id);
    Course updateCourse(Long id, CourseUpdateDto courseUpdateDto, UserDetails currentUser);
    void deleteCourse(Long id, UserDetails currentUser);
}