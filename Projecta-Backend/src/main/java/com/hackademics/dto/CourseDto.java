package com.hackademics.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourseDto {

    @NotNull
    private Long adminId;

    @NotNull
    private Long subjectId; 

    @NotBlank
    private String courseName;

    @NotNull
    private LocalDateTime startDate; 

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private Integer enrollLimit;

    @NotBlank
    private String courseNumber;

    // Constructor

    public CourseDto(Long adminId, Long subjectId, String courseName, LocalDateTime startDate, LocalDateTime endDate, int enrollLimit,
            String courseNumber) {
                this.adminId = adminId;
                this.subjectId = subjectId;
                this.courseName = courseName;
                this.startDate = startDate;
                this.endDate = endDate;
                this.enrollLimit = enrollLimit;
                this.courseNumber = courseNumber;
    }

    // Getters and Setters

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getEnrollLimit() {
        return enrollLimit;
    }

    public void setEnrollLimit(Integer enrollLimit) {
        this.enrollLimit = enrollLimit;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }
        
}
