package com.hackademics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hackademics.model.Enrollment;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.model.WaitlistRequest;
import com.hackademics.service.MailService;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.sending.enabled:true}")
    private boolean emailSendingEnabled;

    @Override
    public void sendEnrollmentEmail(Enrollment enrollment) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping enrollment email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(enrollment.getStudent().getEmail());
        message.setSubject("Enrollment Confirmation");
        message.setText("You have been enrolled in the course " + enrollment.getCourse().getCourseName() + " for the term " + enrollment.getCourse().getTerm());
        javaMailSender.send(message);
        System.out.println("Enrollment email sent to " + enrollment.getStudent().getEmail());
    }

    @Override
    public void sendWaitlistEmail(WaitlistEnrollment waitlistEnrollment) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping waitlist email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(waitlistEnrollment.getStudent().getEmail());
        message.setSubject("Waitlist Confirmation");
        message.setText("You have been added to the waitlist for the course " + waitlistEnrollment.getWaitlist().getCourse().getCourseName() + " for the term " + waitlistEnrollment.getTerm());
        javaMailSender.send(message);
    }

    @Override
    public void sendEnrollmentRemovalEmail(Enrollment enrollment) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping enrollment removal email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(enrollment.getStudent().getEmail());
        message.setSubject("Enrollment Removal Confirmation");
        message.setText("You have been removed from the course " + enrollment.getCourse().getCourseName() + " for the term " + enrollment.getCourse().getTerm());
        javaMailSender.send(message);
    }

    @Override
    public void sendWaitlistRemovalEmail(WaitlistEnrollment waitlistEnrollment) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping waitlist removal email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(waitlistEnrollment.getStudent().getEmail());
        message.setSubject("Waitlist Removal Confirmation");
        message.setText("You have been removed from the waitlist for the course " + waitlistEnrollment.getWaitlist().getCourse().getCourseName() + " for the term " + waitlistEnrollment.getTerm());
        javaMailSender.send(message);
    }

    @Override
    public void sendWaitlistRequestEmail(WaitlistRequest waitlistRequest) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping waitlist request email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(waitlistRequest.getStudent().getEmail());
        message.setSubject("Waitlist Request Confirmation");
        message.setText("You have been added to the waitlist for the course " + waitlistRequest.getWaitlist().getCourse().getCourseName() + " for the term " + waitlistRequest.getWaitlist().getCourse().getTerm());
        javaMailSender.send(message);
    }

}
