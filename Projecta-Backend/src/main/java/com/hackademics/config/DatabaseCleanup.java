package com.hackademics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistEnrollmentRepository;
import com.hackademics.repository.WaitlistRepository;

@Component
@ConditionalOnProperty(name = "app.database.cleanup", havingValue = "true")
@Order(1) // Ensure this runs before DatabasePreloader
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
    private WaitlistRepository waitlistRepository;


    @Override
    public void run(String... args) {
        System.out.println("Cleaning up database...");
        
        // Delete all data in reverse order of dependencies
        waitlistEnrollmentRepository.deleteAll();
        waitlistRepository.deleteAll();
        enrollmentRepository.deleteAll();
        gradeRepository.deleteAll();
        labSectionRepository.deleteAll();
        courseRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
        
        System.out.println("Database cleanup completed.");
    }
} 