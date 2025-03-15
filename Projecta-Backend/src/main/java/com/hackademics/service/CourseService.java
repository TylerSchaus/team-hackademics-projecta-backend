package com.hackademics.service;

import java.util.List;

import com.hackademics.model.Course;

public interface CourseService {
    Course saveCourse(Course course);
    List<Course> getAllCourses();
    Course getCourseById(Long id);
    Course updateCourse(Course course);
    void deleteCourse(Long id);
}