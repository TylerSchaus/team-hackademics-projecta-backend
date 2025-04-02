package com.hackademics.util;

import org.springframework.stereotype.Component;

import com.hackademics.model.Enrollment;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.model.WaitlistRequest;
import com.hackademics.service.MailService;

@Component
public class EmailSender {
    
    private final MailService mailService;

    public EmailSender(MailService mailService) {
        this.mailService = mailService;
    }

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

    public void sendWaitlistRequestEmail(WaitlistRequest waitlistRequest){
        mailService.sendWaitlistRequestEmail(waitlistRequest);
    }
}
