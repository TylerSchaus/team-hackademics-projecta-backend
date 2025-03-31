package com.hackademics.dto.RequestDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WaitlistDto {
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    public WaitlistDto() {
    }

    public WaitlistDto(Long courseId, Integer capacity) {
        this.courseId = courseId;
        this.capacity = capacity;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
} 