package com.hackademics.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hackademics.model.Student;
import com.hackademics.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get a student by ID
    public Optional<Student> getStudentById(Long studentId) {
        return studentRepository.findById(studentId);
    }

    // Create a new student
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    // Update an existing student
    public Student updateStudent(Long studentId, Student updatedStudent) {
        return studentRepository.findById(studentId)
                .map(student -> {
                    student.setBirthDate(updatedStudent.getBirthDate());
                    student.setEnrollmentDate(updatedStudent.getEnrollmentDate());
                    student.setExpectedGradDate(updatedStudent.getExpectedGradDate());
                    return studentRepository.save(student);
                })
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
    }

    // Delete a student by ID
    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }
}