package com.hackademics.config;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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

    @Override
    public void run(String... args) {
        // Check if database is empty
        User admin = new User("Admin", "Test", "admin@test.com", "testPassword", Role.ADMIN, 1L);
        User student1 = new User("Student", "Test", "student@test.com", "testPassword", Role.STUDENT, 1L);
        User student2 = new User("Student2", "Test", "student2@test.com", "testPassword", Role.STUDENT, 2L);
        if (userRepository.count() == 0) {
        
            userRepository.save(admin);
            userRepository.save(student1);
            userRepository.save(student2);
    
            System.out.println("Users was empty. Preloaded with initial data.");
        } 
        else {
            System.out.println("Users already contains data. Skipping preload.");
        }

        Subject subject1 = new Subject("Computer Science", "COSC");
        Subject subject2 = new Subject("Mathematics", "MATH");
        Subject subject3 = new Subject("Physics", "PHYS");
        if (subjectRepository.count() == 0){
   
            subjectRepository.save(subject1);
            subjectRepository.save(subject2);
            subjectRepository.save(subject3);

            System.out.println("Subjects was empty. Preloaded with initial data.");
        } 
        else {
            System.out.println("Subjects already contains data. Skipping preload.");
        }

        Course course1 = new Course(admin, subject1, "Introduction to programming I", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "101", 5, LocalTime.of(10, 0), LocalTime.of(11, 0));
        Course course2 = new Course(admin, subject2, "Calculus I", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "101", 10, LocalTime.of(11, 0), LocalTime.of(12, 0));
        Course course3 = new Course(admin, subject3, "Physics I", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "101", 5, LocalTime.of(8, 0), LocalTime.of(9, 0));
        Course course4 = new Course(admin, subject1, "Introduction to programming II", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "102", 5, LocalTime.of(13, 0), LocalTime.of(14, 0));
        Course course5 = new Course(admin, subject2, "Calculus II", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "102", 20, LocalTime.of(14, 0), LocalTime.of(15, 0));
        Course course6 = new Course(admin, subject3, "Physics II", LocalDateTime.of(2024, 9, 1, 0, 0), LocalDateTime.of(2024, 12, 15, 0, 0), 20, "102", 10, LocalTime.of(15, 0), LocalTime.of(16, 0));
        if (courseRepository.count() == 0){
           
            courseRepository.save(course1);
            courseRepository.save(course2);
            courseRepository.save(course3);
            courseRepository.save(course4);
            courseRepository.save(course5);
            courseRepository.save(course6);

            System.out.println("Courses was empty. Preloaded with initial data.");
        } 
        else {
            System.out.println("Courses already contains data. Skipping preload.");
        }

        LabSection labSection1 = new LabSection(1L, 10, course1, 16, LocalTime.of(10, 0), LocalTime.of(11, 0));
        LabSection labSection2 = new LabSection(2L, 10, course2, 1, LocalTime.of(11, 0), LocalTime.of(12, 0));
        LabSection labSection3 = new LabSection(3L, 10, course3, 8, LocalTime.of(8, 0), LocalTime.of(9, 0));
        
        if (labSectionRepository.count() == 0){

            labSectionRepository.save(labSection1);
            labSectionRepository.save(labSection2);
            labSectionRepository.save(labSection3);

            System.out.println("LabSections was empty. Preloaded with initial data.");
        } 
        else {
            System.out.println("LabSections already contains data. Skipping preload.");
        }
        
        Enrollment enrollment = new Enrollment(course1, student1, labSection1); 
        Enrollment enrollment2 = new Enrollment(course4, student1, null);
        Enrollment enrollment3 = new Enrollment(course5, student1, null);

        if (enrollmentRepository.count() == 0){

            enrollmentRepository.save(enrollment);
            enrollmentRepository.save(enrollment2);
            enrollmentRepository.save(enrollment3);

            System.out.println("Enrollments was empty. Preloaded with initial data.");
        } 
        else {
            System.out.println("Enrollments already contains data. Skipping preload.");
        }

        Grade grade1 = new Grade(student1, course2, 78.0);
        Grade grade2 = new Grade(student1, course3, 85.0);
        Grade grade3 = new Grade(student1, course6, 90.0);

        if (gradeRepository.count() == 0){

            gradeRepository.save(grade1);
            gradeRepository.save(grade2);
            gradeRepository.save(grade3);

            System.out.println("Grades was empty. Preloaded with initial data.");
        } 
        else {
            System.out.println("Grades already contains data. Skipping preload.");
        }

        System.out.println("Database preloaded with initial data.");


    }
} 