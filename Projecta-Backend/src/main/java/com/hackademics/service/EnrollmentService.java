package com.hackademics.service;

import java.util.List;

import com.hackademics.model.Enrollment;

public interface EnrollmentService {
    Enrollment saveEnrollment(Enrollment enrollment);
    List<Enrollment> getAllEnrollments();
    Enrollment getEnrollmentById(Long id);
    Enrollment updateEnrollment(Enrollment enrollment);
    void deleteEnrollment(Long id);
}