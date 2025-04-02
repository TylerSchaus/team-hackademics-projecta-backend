package com.hackademics.dto.RequestDto;

import jakarta.validation.constraints.NotNull;

public class WaitlistEnrollmentDto {
    @NotNull(message = "Waitlist ID is required")
    private Long waitlistId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    public WaitlistEnrollmentDto(){}

    public WaitlistEnrollmentDto(Long waitlistId, Long studentId){
        this.waitlistId = waitlistId;
        this.studentId = studentId;
    }

    public Long getWaitlistId(){
        return waitlistId;
        }

    public void setWaitlistId(Long waitlistId){
        this.waitlistId = waitlistId;
    }

    public Long getStudentId(){
        return studentId;
    }

    public void setStudentId(Long studentId){
        this.studentId = studentId;
    }
    
} 