package com.hackademics.dto.ResponseDto;

public class WaitlistSummaryDto {
    private Long waitlistId;
    private Integer capacity;
    private Integer currentEnroll;

    public WaitlistSummaryDto() {
    }

    public WaitlistSummaryDto(Long waitlistId, Integer capacity, Integer currentEnroll) {
        this.waitlistId = waitlistId;
        this.capacity = capacity;
        this.currentEnroll = currentEnroll;
    }

    public Long getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(Long waitlistId) {
        this.waitlistId = waitlistId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getCurrentEnroll() {
        return currentEnroll;
    }
    
    public void setCurrentEnroll(Integer currentEnroll) {
        this.currentEnroll = currentEnroll;
    }
    
} 