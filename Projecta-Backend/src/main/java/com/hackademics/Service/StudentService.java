package com.hackademics.Service;
import com.hackademics.Model.Student;
import com.hackademics.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
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