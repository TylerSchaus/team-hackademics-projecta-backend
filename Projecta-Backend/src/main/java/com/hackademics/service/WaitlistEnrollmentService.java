package com.hackademics.service;

import com.hackademics.model.WaitlistEnrollment;
import java.util.List;

public interface WaitlistEnrollmentService {
    WaitlistEnrollment saveWaitlistEnrollment(WaitlistEnrollment waitlistEnrollment);
    List<WaitlistEnrollment> getAllWaitlistEnrollments();
    WaitlistEnrollment getWaitlistEnrollmentById(Long id);
    WaitlistEnrollment updateWaitlistEnrollment(WaitlistEnrollment waitlistEnrollment);
    void deleteWaitlistEnrollment(Long id);
}

