package com.hackademics.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.CourseDto;
import com.hackademics.dto.CourseUpdateDto;
import com.hackademics.model.Course;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Course saveCourse(CourseDto courseDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create courses.");
        }
        Course newCourse = new Course(
                userRepository.findById(courseDto.getAdminId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found")),
                subjectRepository.findById(courseDto.getSubjectId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found")),
                courseDto.getCourseName(),
                courseDto.getStartDate(),
                courseDto.getEndDate(),
                courseDto.getEnrollLimit(),
                courseDto.getCourseNumber());
        return courseRepository.save(newCourse);
    }

    @Override
    public List<Course> getAllActiveCourses() {
        LocalDateTime now = LocalDateTime.now();
        return courseRepository.findByStartDateBeforeAndEndDateAfter(now, now);
    }

    @Override
    public List<Course> getAllUpcomingCourses() {
        LocalDateTime now = LocalDateTime.now();
        return courseRepository.findByStartDateAfter(now);
    }

    @Override
    public List<Course> getAllCoursesByAdmin(UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view their courses.");
        }

        return courseRepository.findByAdminId(authenticatedUser.getId());
    }

    @Override
    public List<Course> getAllCoursesBySubjectId(Long id) {
        return courseRepository.findBySubjectId(id);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + id));
    }

    @Override
    public Course updateCourse(Long id, CourseUpdateDto courseUpdateDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update courses.");
        }

        return courseRepository.findById(id).map(course -> {

            if (courseUpdateDto.getAdminId() != null) {
                User newAdmin = userRepository.findById(courseUpdateDto.getAdminId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assigned Admin not found"));
                course.setAdmin(newAdmin);
            }

            if (courseUpdateDto.getCourseName() != null) {
                course.setCourseName(courseUpdateDto.getCourseName());
            }

            if (courseUpdateDto.getCourseNumber() != null) {
                course.setCourseNumber(courseUpdateDto.getCourseNumber());
            }

            if (courseUpdateDto.getSubjectId() != null) {
                Subject newSubject = subjectRepository.findById(courseUpdateDto.getSubjectId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
                course.setSubject(newSubject);
            }

            if (courseUpdateDto.getEnrollLimit() != null) {
                course.setEnrollLimit(courseUpdateDto.getEnrollLimit());
            }

            if (courseUpdateDto.getStartDate() != null) {
                course.setStartDate(courseUpdateDto.getStartDate());

                if (courseUpdateDto.getStartDate().getMonth() != null) {
                    course.setSemester(
                            switch (courseUpdateDto.getStartDate().getMonth()) {
                        case SEPTEMBER ->
                            1;
                        case JANUARY ->
                            2;
                        default ->
                            3;
                    });
                } else {
                    course.setSemester(3); // Default semester
                }
            }

            if (courseUpdateDto.getEndDate() != null) {
                course.setEndDate(courseUpdateDto.getEndDate());
            }

            if (courseUpdateDto.getSubjectId() != null || courseUpdateDto.getCourseNumber() != null) {
                course.setCourseTag(course.getSubject().getSubjectTag() + " " + course.getCourseNumber());
            }

            return courseRepository.save(course);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    @Override
    public void deleteCourse(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete courses.");
        }

        if (!courseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
        }

        courseRepository.deleteById(id);
    }
}
