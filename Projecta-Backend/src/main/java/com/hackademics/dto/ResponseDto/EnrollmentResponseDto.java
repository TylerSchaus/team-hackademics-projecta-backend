package com.hackademics.dto.ResponseDto;

public class EnrollmentResponseDto {
    private Long id;
    private CourseResponseDto course;
    private StudentSummaryDto student;
    private LabSectionResponseDto labSection;

    public EnrollmentResponseDto() {
    }

    public EnrollmentResponseDto(Long id, CourseResponseDto course, StudentSummaryDto student,
            LabSectionResponseDto labSection) {
        this.id = id;
        this.course = course;
        this.student = student;
        this.labSection = labSection;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseResponseDto getCourse() {
        return course;
    }

    public void setCourse(CourseResponseDto course) {
        this.course = course;
    }

    public StudentSummaryDto getStudent() {
        return student;
    }

    public void setStudent(StudentSummaryDto student) {
        this.student = student;
    }

    public LabSectionResponseDto getLabSection() {
        return labSection;
    }

    public void setLabSection(LabSectionResponseDto labSection) {
        this.labSection = labSection;
    }
} 