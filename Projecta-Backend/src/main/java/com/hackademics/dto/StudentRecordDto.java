package com.hackademics.dto;

import java.time.LocalDateTime;
import java.util.List;

public class StudentRecordDto {
    private String studentName;
    private Long studentNumber;
    private LocalDateTime enrollmentDate;
    private List<CurrentEnrollmentDto> currentEnrollments;
    private List<CompletedCourseDto> completedCourses;
    private AcademicPerformanceDto academicPerformance;

    public StudentRecordDto() {
    }

    public StudentRecordDto(String studentName, Long studentNumber, LocalDateTime enrollmentDate,
            List<CurrentEnrollmentDto> currentEnrollments, List<CompletedCourseDto> completedCourses,
            AcademicPerformanceDto academicPerformance) {
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.enrollmentDate = enrollmentDate;
        this.currentEnrollments = currentEnrollments;
        this.completedCourses = completedCourses;
        this.academicPerformance = academicPerformance;
    }

    // Getters and Setters
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Long studentNumber) {
        this.studentNumber = studentNumber;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public List<CurrentEnrollmentDto> getCurrentEnrollments() {
        return currentEnrollments;
    }

    public void setCurrentEnrollments(List<CurrentEnrollmentDto> currentEnrollments) {
        this.currentEnrollments = currentEnrollments;
    }

    public List<CompletedCourseDto> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(List<CompletedCourseDto> completedCourses) {
        this.completedCourses = completedCourses;
    }

    public AcademicPerformanceDto getAcademicPerformance() {
        return academicPerformance;
    }

    public void setAcademicPerformance(AcademicPerformanceDto academicPerformance) {
        this.academicPerformance = academicPerformance;
    }

    // Inner DTOs
    public static class CurrentEnrollmentDto {
        private String courseTag;
        private String courseName;
        private String term;

        public CurrentEnrollmentDto(String courseTag, String courseName, String term) {
            this.courseTag = courseTag;
            this.courseName = courseName;
            this.term = term;
        }

        // Getters and Setters
        public String getCourseTag() {
            return courseTag;
        }

        public void setCourseTag(String courseTag) {
            this.courseTag = courseTag;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }

    public static class CompletedCourseDto {
        private String courseTag;
        private String courseName;
        private Double grade;

        public CompletedCourseDto(String courseTag, String courseName, Double grade) {
            this.courseTag = courseTag;
            this.courseName = courseName;
            this.grade = grade;
        }

        // Getters and Setters
        public String getCourseTag() {
            return courseTag;
        }

        public void setCourseTag(String courseTag) {
            this.courseTag = courseTag;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public Double getGrade() {
            return grade;
        }

        public void setGrade(Double grade) {
            this.grade = grade;
        }
    }

    public static class AcademicPerformanceDto {
        private Integer completedCoursesCount;
        private Double gpa;
        private Double percentage;

        public AcademicPerformanceDto(Integer completedCoursesCount, Double gpa, Double percentage) {
            this.completedCoursesCount = completedCoursesCount;
            this.gpa = gpa;
            this.percentage = percentage;
        }

        // Getters and Setters
        public Integer getCompletedCoursesCount() {
            return completedCoursesCount;
        }

        public void setCompletedCoursesCount(Integer completedCoursesCount) {
            this.completedCoursesCount = completedCoursesCount;
        }

        public Double getGpa() {
            return gpa;
        }

        public void setGpa(Double gpa) {
            this.gpa = gpa;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }
}