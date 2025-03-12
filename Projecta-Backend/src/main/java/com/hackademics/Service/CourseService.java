package com.hackademics.Service;

import com.hackademics.model.Course;
import java.util.List;

public interface CourseService {
    Course saveCourse(Course course);
    List<Course> getAllCourses();
    Course getCourseById(Long id);
    Course updateCourse(Course course);
    void deleteCourse(Long id);
}
