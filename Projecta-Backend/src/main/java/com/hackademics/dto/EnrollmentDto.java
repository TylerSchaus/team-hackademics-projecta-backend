package com.hackademics.dto;

import jakarta.validation.constraints.NotNull;

public class EnrollmentDto {
    
    @NotNull
    private Long courseId;
    
    @NotNull
    private Long studentId;

    public EnrollmentDto(Long courseId, Long studentId){
        this.courseId = courseId;
        this.studentId = studentId;
    } 

    public Long getCourseId() {
        return courseId;
    }
    public Long getStudentId() {
        return studentId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

}
