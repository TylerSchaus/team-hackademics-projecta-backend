package com.hackademics.dto.ResponseDto;

public class StudentSummaryDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Long studentId;
    private String major;

    public StudentSummaryDto() {
    }

    public StudentSummaryDto(Long id, String firstName, String lastName, Long studentId, String major) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.major = major;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }
    
    public void setMajor(String major) {
        this.major = major;
    }
} 