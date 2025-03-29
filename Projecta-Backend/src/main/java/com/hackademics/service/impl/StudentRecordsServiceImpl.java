package com.hackademics.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.StudentRecordDto;
import com.hackademics.dto.StudentRecordDto.AcademicPerformanceDto;
import com.hackademics.dto.StudentRecordDto.CompletedCourseDto;
import com.hackademics.dto.StudentRecordDto.CurrentEnrollmentDto;
import com.hackademics.model.Grade;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.StudentRecordsService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentRecordsServiceImpl implements StudentRecordsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public StudentRecordDto getStudentRecord(Long studentId, UserDetails currentUser) {

        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() == Role.STUDENT && !authenticatedUser.getStudentId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only view their own records.");
        }

        User student = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // Get current enrollments
        List<CurrentEnrollmentDto> currentEnrollments = student.getEnrollments().stream()
                .map(enrollment -> new CurrentEnrollmentDto(
                enrollment.getCourse().getCourseTag(),
                enrollment.getCourse().getCourseName(),
                enrollment.getCourse().getTerm()))
                .collect(Collectors.toList());

        // Get completed courses
        List<CompletedCourseDto> completedCourses = student.getGrades().stream()
                .map(grade -> new CompletedCourseDto(
                grade.getCourseTagCopy(),
                grade.getCourseNameCopy(),
                grade.getGrade()))
                .collect(Collectors.toList());

        // Calculate academic performance
        double totalGrade = student.getGrades().stream()
                .mapToDouble(Grade::getGrade)
                .sum();
        double averageGrade = student.getGrades().isEmpty() ? 0 : totalGrade / student.getGrades().size();
        double gpa = convertToGPA(averageGrade);

        AcademicPerformanceDto academicPerformance = new AcademicPerformanceDto(
                student.getGrades().size(),
                gpa,
                averageGrade);

        return new StudentRecordDto(
                student.getFirstName() + " " + student.getLastName(),
                student.getStudentId(),
                student.getEnrollStartDate(),
                currentEnrollments,
                completedCourses,
                academicPerformance);
    }

    @Override
    public String getStudentRecordAsText(Long studentId, UserDetails currentUser) {
        StudentRecordDto record = getStudentRecord(studentId, currentUser);
        StringBuilder sb = new StringBuilder();

        System.out.println("String builder initialized, got past dto creation.");

        // Student Info
        sb.append(record.getStudentName()).append("\n");
        sb.append(record.getStudentNumber()).append("\n");
        sb.append("Enrolled on: ").append(formatDate(record.getEnrollmentDate())).append("\n\n");

        // Current Enrollments
        sb.append("Current Enrollments:\n");
        record.getCurrentEnrollments().forEach(enrollment
                -> sb.append(String.format("%s, %s, %s\n",
                        enrollment.getCourseTag(),
                        enrollment.getCourseName(),
                        enrollment.getTerm()))
        );
        sb.append("\n");

        // Completed Courses
        sb.append("Completed Courses:\n");
        record.getCompletedCourses().forEach(course
                -> sb.append(String.format("%s, %s, %.0f%%\n",
                        course.getCourseTag(),
                        course.getCourseName(),
                        course.getGrade()))
        );
        sb.append("\n");

        // Academic Performance
        sb.append("Academic Performance:\n");
        sb.append("Completed Courses:\n");
        sb.append(record.getAcademicPerformance().getCompletedCoursesCount()).append("\n\n");
        sb.append("GPA:\n");
        sb.append(String.format("%.1f / %.0f%%\n",
                record.getAcademicPerformance().getGpa(),
                record.getAcademicPerformance().getPercentage()));

        System.out.println("String builder completed, got past stringbuilder creation.");

        return sb.toString();
    }

    @Override
    public String getStudentRecordAsCsv(Long studentId, UserDetails currentUser) {
        StudentRecordDto record = getStudentRecord(studentId, currentUser);
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("Student Name,Student Number,Enrollment Date\n");
        sb.append(String.format("%s,%d,%s\n\n",
                record.getStudentName(),
                record.getStudentNumber(),
                formatDate(record.getEnrollmentDate())));

        // Current Enrollments
        sb.append("Current Enrollments\n");
        sb.append("Course Tag,Course Name,Term\n");
        record.getCurrentEnrollments().forEach(enrollment
                -> sb.append(String.format("%s,%s,%s\n",
                        enrollment.getCourseTag(),
                        enrollment.getCourseName(),
                        enrollment.getTerm()))
        );
        sb.append("\n");

        // Completed Courses
        sb.append("Completed Courses\n");
        sb.append("Course Tag,Course Name,Grade\n");
        record.getCompletedCourses().forEach(course
                -> sb.append(String.format("%s,%s,%.0f\n",
                        course.getCourseTag(),
                        course.getCourseName(),
                        course.getGrade()))
        );
        sb.append("\n");

        // Academic Performance
        sb.append("Academic Performance\n");
        sb.append("Completed Courses Count,GPA,Percentage\n");
        sb.append(String.format("%d,%.1f,%.0f\n",
                record.getAcademicPerformance().getCompletedCoursesCount(),
                record.getAcademicPerformance().getGpa(),
                record.getAcademicPerformance().getPercentage()));

        return sb.toString();
    }

    private String formatDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private double convertToGPA(double percentage) {
        double gpa;

        if (percentage < 50) {
            gpa = 0.0;
        } else {
            gpa = 1.0 + (percentage - 50) * (4.3 - 1.0) / (90 - 50);
            gpa = Math.min(gpa, 4.3);
        }

        return Math.round(gpa * 10.0) / 10.0;
    }

}
