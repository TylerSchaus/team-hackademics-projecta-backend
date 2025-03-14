package com.hackademics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.EnrollmentDto;
import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired 
    private CourseRepository courseRepository;

    @Override
    public Enrollment saveEnrollment(EnrollmentDto enrollmentDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Course course = courseRepository.findById(enrollmentDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        User student = userRepository.findById(enrollmentDto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (authenticatedUser.getRole() != Role.ADMIN && !authenticatedUser.getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only enroll themselves.");
        }

        if (course.getCurrentEnroll() >= course.getEnrollLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is at full capacity.");
        }

        Enrollment enrollment = new Enrollment(course, student);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        course.setCurrentEnroll(course.getCurrentEnroll() + 1);
        courseRepository.save(course);

        return savedEnrollment;
    }

    @Override
    public List<Enrollment> getAllEnrollments(UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all enrollments.");
        }

        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
        }

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view enrollments for a course.");
        }

        return enrollmentRepository.findByCourseId(courseId);
    }

    @Override
    public Enrollment getEnrollmentById(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view specific enrollments.");
        }

        return enrollment;
    }

    @Override
    public void deleteEnrollment(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        Course course = enrollment.getCourse();

        if (authenticatedUser.getRole() != Role.ADMIN && !authenticatedUser.getId().equals(enrollment.getStudent().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only delete their own enrollments.");
        }

        enrollmentRepository.deleteById(id);

        if (course.getCurrentEnroll() > 0) {
            course.setCurrentEnroll(course.getCurrentEnroll() - 1);
            courseRepository.save(course);
        }
    }
}
