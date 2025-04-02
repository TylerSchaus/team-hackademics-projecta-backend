package com.hackademics.dto.ResponseDto;

import java.time.LocalDate;

public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private Long studentId;
    private LocalDate enrollStartDate;
    private LocalDate expectGraduationDate;
    private Long adminId;
    private String major;


    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String firstName, String lastName, String email, String phoneNumber, String role, 
            Long studentId, LocalDate enrollStartDate, LocalDate expectGraduationDate, Long adminId, String major) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.studentId = studentId;
        this.enrollStartDate = enrollStartDate;
        this.expectGraduationDate = expectGraduationDate;
        this.adminId = adminId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public LocalDate getEnrollStartDate() {
        return enrollStartDate;
    }

    public void setEnrollStartDate(LocalDate enrollStartDate) {
        this.enrollStartDate = enrollStartDate;
    }

    public LocalDate getExpectGraduationDate() {
        return expectGraduationDate;
    }

    public void setExpectGraduationDate(LocalDate expectGraduationDate) {
        this.expectGraduationDate = expectGraduationDate;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
} 