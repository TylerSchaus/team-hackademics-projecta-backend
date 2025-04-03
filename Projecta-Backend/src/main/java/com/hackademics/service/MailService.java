package com.hackademics.service;

import com.hackademics.model.Enrollment;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.model.WaitlistRequest;

public interface MailService {
    
    void sendEnrollmentEmail(Enrollment enrollment); 
    void sendWaitlistEmail(WaitlistEnrollment waitlistEnrollment);
    void sendEnrollmentRemovalEmail(Enrollment enrollment);
    void sendWaitlistRemovalEmail(WaitlistEnrollment waitlistEnrollment);
    void sendWaitlistRequestEmail(WaitlistRequest waitlistRequest);
}
