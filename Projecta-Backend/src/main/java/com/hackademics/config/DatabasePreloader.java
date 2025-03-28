package com.hackademics.config;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.Grade;
import com.hackademics.model.LabSection;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.GradeRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;

@Component
@Order(2) // Ensure this runs after DatabaseCleanup
public class DatabasePreloader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) {
        // Create and save users
        User admin = new User("Admin", "Test", "admin@test.com", bCryptPasswordEncoder.encode("testPassword"), Role.ADMIN, 1L);
        User student1 = new User("Student", "Test", "student@test.com", bCryptPasswordEncoder.encode("testPassword"), Role.STUDENT, 1L);
        User student2 = new User("Student2", "Test", "student2@test.com", bCryptPasswordEncoder.encode("testPassword"), Role.STUDENT, 2L);
        
        User savedAdmin = userRepository.save(admin);
        User savedStudent1 = userRepository.save(student1);
        userRepository.save(student2);
        System.out.println("Users saved to database.");

        // Create and save subjects
        Subject subject1 = new Subject("Computer Science", "COSC");
        Subject subject2 = new Subject("Mathematics", "MATH");
        Subject subject3 = new Subject("Physics", "PHYS");
        
        Subject savedSubject1 = subjectRepository.save(subject1);
        Subject savedSubject2 = subjectRepository.save(subject2);
        Subject savedSubject3 = subjectRepository.save(subject3);
        System.out.println("Subjects saved to database.");

        // Create and save courses using saved entities
        Course course1 = new Course(savedAdmin, savedSubject1, "Introduction to programming I", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "101", 5, LocalTime.of(10, 0), LocalTime.of(11, 0));
        Course course2 = new Course(savedAdmin, savedSubject2, "Calculus I", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "101", 10, LocalTime.of(11, 0), LocalTime.of(12, 0));
        Course course3 = new Course(savedAdmin, savedSubject3, "Physics I", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "101", 5, LocalTime.of(8, 0), LocalTime.of(9, 0));
        Course course4 = new Course(savedAdmin, savedSubject1, "Introduction to programming II", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "102", 5, LocalTime.of(13, 0), LocalTime.of(14, 0));
        Course course5 = new Course(savedAdmin, savedSubject2, "Calculus II", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "102", 20, LocalTime.of(14, 0), LocalTime.of(15, 0));
        Course course6 = new Course(savedAdmin, savedSubject3, "Physics II", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "102", 10, LocalTime.of(15, 0), LocalTime.of(16, 0));
        
        Course savedCourse1 = courseRepository.save(course1);
        Course savedCourse2 = courseRepository.save(course2);
        Course savedCourse3 = courseRepository.save(course3);
        Course savedCourse4 = courseRepository.save(course4);
        Course savedCourse5 = courseRepository.save(course5);
        Course savedCourse6 = courseRepository.save(course6);
        System.out.println("Courses saved to database.");

        // Create and save lab sections using saved course entities
        LabSection labSection1 = new LabSection(1L, 10, savedCourse1, 16, LocalTime.of(10, 0), LocalTime.of(11, 0));
        LabSection labSection2 = new LabSection(2L, 10, savedCourse2, 1, LocalTime.of(11, 0), LocalTime.of(12, 0));
        LabSection labSection3 = new LabSection(3L, 10, savedCourse3, 8, LocalTime.of(8, 0), LocalTime.of(9, 0));
        
        LabSection savedLabSection1 = labSectionRepository.save(labSection1);
        labSectionRepository.save(labSection2);
        labSectionRepository.save(labSection3);
        System.out.println("Lab sections saved to database.");
        
        // Create and save enrollments using saved entities
        Enrollment enrollment = new Enrollment(savedCourse1, savedStudent1, savedLabSection1); 
        Enrollment enrollment2 = new Enrollment(savedCourse4, savedStudent1, null);
        Enrollment enrollment3 = new Enrollment(savedCourse5, savedStudent1, null);

        enrollmentRepository.save(enrollment);
        enrollmentRepository.save(enrollment2);
        enrollmentRepository.save(enrollment3);
        System.out.println("Enrollments saved to database.");

        // Create and save grades using saved entities
        Grade grade1 = new Grade(savedStudent1, savedCourse2, 78.0);
        Grade grade2 = new Grade(savedStudent1, savedCourse3, 85.0);
        Grade grade3 = new Grade(savedStudent1, savedCourse6, 90.0);

        gradeRepository.save(grade1);
        gradeRepository.save(grade2);
        gradeRepository.save(grade3);
        System.out.println("Grades saved to database.");

        System.out.println("Database preloaded with initial data.");
    }
} 