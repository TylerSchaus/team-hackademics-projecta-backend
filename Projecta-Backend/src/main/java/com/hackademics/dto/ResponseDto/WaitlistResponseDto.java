package com.hackademics.dto.ResponseDto;

public class WaitlistResponseDto {
    private Long waitlistId;
    private Integer capacity;
    private CourseSummaryDto course;

    public WaitlistResponseDto() {
    }

    public WaitlistResponseDto(Long waitlistId, Integer capacity, CourseSummaryDto course) {
        this.waitlistId = waitlistId;
        this.capacity = capacity;
        this.course = course;
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

    public CourseSummaryDto getCourse() {
        return course;
    }

    public void setCourse(CourseSummaryDto course) {
        this.course = course;
    }
} 