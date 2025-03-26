package com.hackademics.dto;

public class AdminSummaryDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Long adminId;

    public AdminSummaryDto() {
    }

    public AdminSummaryDto(Long id, String firstName, String lastName, Long adminId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.adminId = adminId;
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

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
} 