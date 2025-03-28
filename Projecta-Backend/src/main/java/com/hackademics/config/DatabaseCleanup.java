package com.hackademics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistEnrollmentRepository;

@Component
@ConditionalOnProperty(name = "app.database.cleanup", havingValue = "true")
public class DatabaseCleanup implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private WaitlistEnrollmentRepository waitlistEnrollmentRepository;
    
    @Autowired
    private WaitlistEnrollmentRepository waitlistRepository;


    @Override
    public void run(String... args) {
        System.out.println("Cleaning up database...");
        
        // Delete all data in reverse order of dependencies
        waitlistEnrollmentRepository.deleteAll();
        waitlistRepository.deleteAll();
        labSectionRepository.deleteAll();
        gradeRepository.deleteAll();
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
        
        System.out.println("Database cleanup completed.");
    }
} 