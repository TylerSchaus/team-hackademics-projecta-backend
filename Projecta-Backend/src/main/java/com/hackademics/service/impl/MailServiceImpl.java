package com.hackademics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void sendEnrollmentEmail(EnrollmentResponseDto enrollmentResponseDto, User student) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Course Enrollment Confirmation");
        message.setText("Dear " + student.getFirstName() + ",\n\n" +
                "You have been successfully enrolled in " + enrollmentResponseDto.getCourse().getCourseName() + ".\n\n" +
                "Course Details:\n" +
                "Course: " + enrollmentResponseDto.getCourse().getCourseName() + "\n" +
                "Course Number: " + enrollmentResponseDto.getCourse().getCourseNumber() + "\n" +
                "Term: " + enrollmentResponseDto.getCourse().getTerm() + "\n\n" +
                "Best regards,\nHackademics Team");

        javaMailSender.send(message);
    }

    @Override
    public void sendWaitlistEmail(WaitlistEnrollmentResponseDto waitlistEnrollmentResponseDto, User student) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Waitlist Enrollment Confirmation");
        message.setText("Dear " + student.getFirstName() + ",\n\n" +
                "You have been added to the waitlist for " + waitlistEnrollmentResponseDto.getWaitlistResponseDto().getCourse().getCourseName() + ".\n\n" +
                "Course Details:\n" +
                "Course: " + waitlistEnrollmentResponseDto.getWaitlistResponseDto().getCourse().getCourseName() + "\n" +
                "Course Number: " + waitlistEnrollmentResponseDto.getWaitlistResponseDto().getCourse().getCourseNumber() + "\n" +
                "Term: " + waitlistEnrollmentResponseDto.getWaitlistResponseDto().getCourse().getTerm() + "\n" +
                "Waitlist Position: " + waitlistEnrollmentResponseDto.getWaitlistPosition() + "\n\n" +
                "Best regards,\nHackademics Team");

        javaMailSender.send(message);
    }

    @Override
    public void sendEnrollmentRemovalEmail(EnrollmentResponseDto enrollmentResponseDto, User student) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(student.getEmail());
        message.setSubject("Enrollment Removal Confirmation");
        message.setText("You have been removed from the course " + enrollmentResponseDto.getCourse().getCourseName() + " for the term " + enrollmentResponseDto.getCourse().getTerm());
        javaMailSender.send(message);
    }

    @Override
    public void sendWaitlistRemovalEmail(WaitlistEnrollmentResponseDto waitlistEnrollmentResponseDto, User student) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hackademicsuniversity@gmail.com");
        message.setTo(student.getEmail());
        message.setSubject("Waitlist Removal Confirmation");
        message.setText("You have been removed from the waitlist for the course " + waitlistEnrollmentResponseDto.getWaitlistResponseDto().getCourse().getCourseName() + " for the term " + waitlistEnrollmentResponseDto.getTerm());
        javaMailSender.send(message);
    }
}
