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
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.StudentRecordsService;
import com.hackademics.util.GpaCalculator;
import com.hackademics.util.RoleBasedAccessVerification;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentRecordsServiceImpl implements StudentRecordsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    @Override
    public StudentRecordDto getStudentRecord(Long studentId, UserDetails currentUser) {

        if (!roleBasedAccessVerification.isCurrentUserRequestedStudentOrAdmin(currentUser, studentId)) {
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
        double averageGrade = GpaCalculator.computeGradeAverage(student);
        double gpa = GpaCalculator.convertToGPA(averageGrade);

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

}
