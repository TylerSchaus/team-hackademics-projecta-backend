package com.hackademics.dto;

import jakarta.validation.constraints.NotNull;

public class EnrollmentDto {
    
    @NotNull
    private Long courseId;

    private Long labSectionId;
    
    @NotNull
    private Long studentId;

    public EnrollmentDto(Long studentId, Long courseId, Long labSectionId){
        this.studentId = studentId;
        this.courseId = courseId;
        this.labSectionId = labSectionId;
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

    public Long getLabSectionId() {
        return labSectionId;
    }

    public void setLabSectionId(Long labSectionId) {
        this.labSectionId = labSectionId;
    }

}
