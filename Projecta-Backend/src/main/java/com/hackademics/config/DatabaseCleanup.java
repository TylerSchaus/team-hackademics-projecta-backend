package com.hackademics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.UserRepository;

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

    @Override
    public void run(String... args) {
        System.out.println("Cleaning up database...");
        
        // Delete all data in reverse order of dependencies
        enrollmentRepository.deleteAll();
        gradeRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
        
        System.out.println("Database cleanup completed.");
    }
} 