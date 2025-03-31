package com.hackademics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hackademics.dto.ResponseDto.EnrollmentResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistEnrollmentResponseDto;
import com.hackademics.model.User;
import com.hackademics.service.MailService;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.sending.enabled:true}")
    private boolean emailSendingEnabled;

    @Override
    public void sendEnrollmentEmail(EnrollmentResponseDto enrollmentResponseDto, User student) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping enrollment email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(student.getEmail());
        message.setSubject("Enrollment Confirmation");
        message.setText("You have been enrolled in the course " + enrollmentResponseDto.getCourse().getCourseName() + " for the term " + enrollmentResponseDto.getCourse().getTerm());
        javaMailSender.send(message);
        System.out.println("Enrollment email sent to " + student.getEmail());
    }

    @Override
    public void sendWaitlistEmail(WaitlistEnrollmentResponseDto waitlistEnrollmentResponseDto, User student) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping waitlist email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(student.getEmail());
        message.setSubject("Waitlist Confirmation");
        message.setText("You have been added to the waitlist for the course " + waitlistEnrollmentResponseDto.getWaitlistResponseDto().getCourse().getCourseName() + " for the term " + waitlistEnrollmentResponseDto.getTerm());
        javaMailSender.send(message);
    }

    @Override
    public void sendEnrollmentRemovalEmail(EnrollmentResponseDto enrollmentResponseDto, User student) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping enrollment removal email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(student.getEmail());
        message.setSubject("Enrollment Removal Confirmation");
        message.setText("You have been removed from the course " + enrollmentResponseDto.getCourse().getCourseName() + " for the term " + enrollmentResponseDto.getCourse().getTerm());
        javaMailSender.send(message);
    }

    @Override
    public void sendWaitlistRemovalEmail(WaitlistEnrollmentResponseDto waitlistEnrollmentResponseDto, User student) {
        if (!emailSendingEnabled) {
            System.out.println("Email sending is disabled. Skipping waitlist removal email.");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(student.getEmail());
        message.setSubject("Waitlist Removal Confirmation");
        message.setText("You have been removed from the waitlist for the course " + waitlistEnrollmentResponseDto.getWaitlistResponseDto().getCourse().getCourseName() + " for the term " + waitlistEnrollmentResponseDto.getTerm());
        javaMailSender.send(message);
    }

}
