package com.hackademics.dto.ResponseDto;

public class GradeResponseDto {
    private Long id; 
    private Double grade;
    private StudentSummaryDto student; 
    private CourseResponseDto course;

    public GradeResponseDto() {
    }

    public GradeResponseDto(Long id, Double grade, StudentSummaryDto student, CourseResponseDto course) {
        this.id = id;
        this.grade = grade;
        this.student = student;
        this.course = course;
    }

    public Long getId() {
        return id;
    }
    public Double getGrade() {
        return grade;
    }
    public StudentSummaryDto getStudent() {
        return student;
    }
    public CourseResponseDto getCourse() {
        return course;
    }
    
    
}
