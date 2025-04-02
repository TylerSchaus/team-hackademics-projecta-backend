package com.hackademics.dto.RequestDto;

import jakarta.validation.constraints.NotNull;

public class WaitlistRequestDto {

    @NotNull 
    Long waitlistId; 

    @NotNull
    Long studentId; 

    public WaitlistRequestDto() {
    }

    public WaitlistRequestDto(Long waitlistId, Long studentId) {
        this.waitlistId = waitlistId;
        this.studentId = studentId;
    }

    public Long getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(Long waitlistId) {
        this.waitlistId = waitlistId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
} 