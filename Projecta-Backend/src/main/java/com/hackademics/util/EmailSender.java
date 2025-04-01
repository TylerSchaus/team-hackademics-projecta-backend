package com.hackademics.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.hackademics.model.Enrollment;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.service.MailService;

public class EmailSender {
    
    @Autowired
    private MailService mailService;

    public void sendEnrollmentEmail(Enrollment enrollment){
        mailService.sendEnrollmentEmail(enrollment);
    }

    public void sendWaitlistEmail(WaitlistEnrollment waitlistEnrollment){
        mailService.sendWaitlistEmail(waitlistEnrollment);
    }

    public void sendEnrollmentRemovalEmail(Enrollment enrollment){
        mailService.sendEnrollmentRemovalEmail(enrollment);
    }

    public void sendWaitlistRemovalEmail(WaitlistEnrollment waitlistEnrollment){
        mailService.sendWaitlistRemovalEmail(waitlistEnrollment);
    }
}
