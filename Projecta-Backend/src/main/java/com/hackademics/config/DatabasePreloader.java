package com.hackademics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.UserRepository;

@Component
public class DatabasePreloader implements CommandLineRunner {

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
        // Check if database is empty
        if (userRepository.count() == 0) {
            // Add your preload data here
            // Example:
            // User admin = new User();
            // admin.setEmail("admin@example.com");
            // admin.setPassword(passwordEncoder.encode("admin123"));
            // admin.setRole(Role.ADMIN);
            // userRepository.save(admin);
            
            // Add more preload data as needed
            System.out.println("Database was empty. Preloaded with initial data.");
        } else {
            System.out.println("Database already contains data. Skipping preload.");
        }
    }
} 