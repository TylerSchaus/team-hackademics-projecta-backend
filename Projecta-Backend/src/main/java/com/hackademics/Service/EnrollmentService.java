package com.hackademics.Service;

import com.hackademics.model.Enrollment;
import java.util.List;

public interface EnrollmentService {
    Enrollment saveEnrollment(Enrollment enrollment);
    List<Enrollment> getAllEnrollments();
    Enrollment getEnrollmentById(Long id);
    Enrollment updateEnrollment(Enrollment enrollment);
    void deleteEnrollment(Long id);
}